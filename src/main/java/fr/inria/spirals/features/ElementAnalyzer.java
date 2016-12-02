package fr.inria.spirals.features;

import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtCFlowBreak;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtContinue;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLoop;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtThrow;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.visitor.CtInheritanceScanner;
import spoon.reflect.visitor.CtScanner;

public class ElementAnalyzer {
	private CtElement element;

	public ElementAnalyzer(CtElement element) {
		this.element = element;
	}

	public ChangeAnalyze analyze(final ChangeAnalyze output) {
		element.accept(new CtScanner() {
			@Override
			protected void enter(CtElement e) {
				e.accept(new CtInheritanceScanner() {
					@Override
					public <T> void scanCtVariable(CtVariable<T> v) {
						output.incNbVariable();
						super.scanCtVariable(v);
					}

					@Override
					public <T> void scanCtType(CtType<T> type) {
						output.incNbType();
						super.scanCtType(type);
					}

					@Override
					public void scanCtLoop(CtLoop loop) {
						output.incNbLoop();
						super.scanCtLoop(loop);
					}

					@Override
					public <T> void visitCtInvocation(CtInvocation<T> e) {
						output.incNbInvocation();
						if (e.getExecutable().getDeclaration() == null) {
							output.incNbExternalCall();
						}
						super.visitCtInvocation(e);
					}

					@Override
					public void scanCtCFlowBreak(CtCFlowBreak flowBreak) {
						output.incNbBreak();
						super.scanCtCFlowBreak(flowBreak);
					}

					@Override
					public <R> void visitCtReturn(CtReturn<R> e) {
						output.incNbReturn();
						super.visitCtReturn(e);
					}

					@Override
					public void visitCtContinue(CtContinue e) {
						output.incNbContinue();
						super.visitCtContinue(e);
					}

					@Override
					public void visitCtIf(CtIf e) {
						output.incNbIf();
						super.visitCtIf(e);
					}

					@Override
					public void visitCtComment(CtComment e) {
						output.incNbComment();
						super.visitCtComment(e);
					}

					@Override
					public <T, A extends T> void visitCtAssignment(CtAssignment<T, A> e) {
						output.incNbAssignment();
						super.visitCtAssignment(e);
					}

					@Override
					public <T> void visitCtLiteral(CtLiteral<T> e) {
						output.incNbLiteral();
						super.visitCtLiteral(e);
					}

					@Override
					public <T> void visitCtConstructorCall(CtConstructorCall<T> e) {
						output.incNbConstructorCall();
						super.visitCtConstructorCall(e);
					}

					@Override
					public void visitCtTry(CtTry e) {
						// handled in Explorer
						// output.incNbTry();
						super.visitCtTry(e);
					}

					@Override
					public <T> void visitCtBinaryOperator(CtBinaryOperator<T> e) {
						output.incNbBinary();
						super.visitCtBinaryOperator(e);
					}

					@Override
					public <T> void visitCtUnaryOperator(CtUnaryOperator<T> e) {
						output.incNbUnary();
						super.visitCtUnaryOperator(e);
					}

					@Override
					public <T> void scanCtVariableAccess(CtVariableAccess<T> variableAccess) {
						output.incNbVariableAccess();
						super.scanCtVariableAccess(variableAccess);
					}

					@Override
					public void visitCtThrow(CtThrow e) {
						output.incNbThrow();
						super.visitCtThrow(e);
					}
				});
				super.enter(e);
			}
		});
		return output;
	}


}
