package fr.inria.spirals.main;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.stringparsers.EnumeratedStringParser;
import fr.inria.spirals.entities.Metrics;
import fr.inria.spirals.features.extractor.AstExtractor;
import fr.inria.spirals.features.extractor.MetricExtractor;

import java.util.Iterator;

/**
 * Created by tdurieux
 */
public class Launcher {

    private Config config;

    public Launcher(String[] args) throws JSAPException {
        JSAP jsap = this.initJSAP();
        JSAPResult arguments = this.parseArguments(args, jsap);
        if (arguments == null) {
            System.exit(-1);
        }
        this.initConfig(arguments);
    }

    private void showUsage(JSAP jsap) {
        System.err.println();
        System.err.println("Usage: java -jar patchclustering.jar <options>");
        System.err.println();
        System.err.println("Options:");
        System.err.println();
        System.err.println(jsap.getHelp());
    }

    private JSAPResult parseArguments(String[] args, JSAP jsap) {
        JSAPResult config = jsap.parse(args);
        if (!config.success()) {
            System.err.println();
            for (Iterator<?> errs = config.getErrorMessageIterator(); errs.hasNext();) {
                System.err.println("Error: " + errs.next());
            }
            this.showUsage(jsap);
            return null;
        }
        return config;
    }

    private JSAP initJSAP() throws JSAPException {
        JSAP jsap = new JSAP();

        FlaggedOption opt = new FlaggedOption("bugId");
        opt.setShortFlag('b');
        opt.setLongFlag("bugId");
        opt.setRequired(true);
        opt.setAllowMultipleDeclarations(false);
        opt.setStringParser(JSAP.STRING_PARSER);
        opt.setHelp("The bug id.");
        jsap.registerParameter(opt);

        opt = new FlaggedOption("outputDirectory");
        opt.setShortFlag('o');
        opt.setLongFlag("output");
        opt.setRequired(false);
        opt.setAllowMultipleDeclarations(false);
        opt.setStringParser(JSAP.STRING_PARSER);
        opt.setHelp("The path to output the results.");
        jsap.registerParameter(opt);

        opt = new FlaggedOption("buggySourceDirectory");
        opt.setLongFlag("buggySourceDirectory");
        opt.setRequired(true);
        opt.setAllowMultipleDeclarations(false);
        opt.setStringParser(JSAP.STRING_PARSER);
        opt.setHelp("The path to the buggy source code directory of the project.");
        jsap.registerParameter(opt);

        opt = new FlaggedOption("fixedSourceDirectory");
        opt.setLongFlag("fixedSourceDirectory");
        opt.setRequired(true);
        opt.setAllowMultipleDeclarations(false);
        opt.setStringParser(JSAP.STRING_PARSER);
        opt.setHelp("The path to the fixed source code directory of the project.");
        jsap.registerParameter(opt);

        opt = new FlaggedOption("diffPath");
        opt.setLongFlag("diff");
        opt.setRequired(true);
        opt.setAllowMultipleDeclarations(true);
        opt.setStringParser(JSAP.STRING_PARSER);
        opt.setHelp("The path to the diff file.");
        jsap.registerParameter(opt);

        String launcherModeValues = "";
        for (LauncherMode mode : LauncherMode.values()) {
            launcherModeValues += mode.name()+";";
        }
        launcherModeValues = launcherModeValues.substring(0, launcherModeValues.length()-1);

        opt = new FlaggedOption("launcherMode");
        opt.setShortFlag('m');
        opt.setLongFlag("launcherMode");
        opt.setRequired(true);
        opt.setAllowMultipleDeclarations(false);
        opt.setUsageName(launcherModeValues);
        opt.setStringParser(EnumeratedStringParser.getParser(launcherModeValues));
        opt.setHelp("The launcher mode.");
        jsap.registerParameter(opt);

        return jsap;
    }

    private void initConfig(JSAPResult arguments) {
        this.config = Config.getInstance();
        this.config.setBugId(arguments.getString("bugId"));
        this.config.setOutputDirectoryPath(arguments.getString("outputDirectory"));
        this.config.setBuggySourceDirectoryPath(arguments.getString("buggySourceDirectory"));
        this.config.setFixedSourceDirectoryPath(arguments.getString("fixedSourceDirectory"));
        this.config.setDiffPath(arguments.getString("diffPath"));
        this.config.setLauncherMode(LauncherMode.valueOf(arguments.getString("launcherMode").toUpperCase()));
    }

    private void execute() {
        switch (this.config.getLauncherMode()) {
            case AST:
                AstExtractor astExtractor = new AstExtractor(
                        this.config.getBuggySourceDirectoryPath(),
                        this.config.getDiffPath());

                astExtractor.extract();
                break;
            case METRICS:
                MetricExtractor metricsExtractor = new MetricExtractor(
                        this.config.getBuggySourceDirectoryPath(),
                        this.config.getFixedSourceDirectoryPath(),
                        this.config.getDiffPath());

                Metrics metrics = metricsExtractor.extract();
                System.out.println(metrics.toString());
                break;
        }
    }

    public static void main(String[] args) throws Exception {
        Launcher launcher = new Launcher(args);
        launcher.execute();
    }

}
