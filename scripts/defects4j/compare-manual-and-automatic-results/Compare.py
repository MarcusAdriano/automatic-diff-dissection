#!/usr/bin/env python

from __future__ import print_function
import sys
import os
import json
import re

features = {'repairPatterns': { 'missNullCheckN', 'missNullCheckP', 'singleLine', 'condBlockOthersAdd', 'condBlockRetAdd', 'condBlockExcAdd', 'condBlockRem' }}

# This function is responsible to find the info from a given bug in the big JSON file from Defects4J dissection
def find_info_from_bug(bug_id, manual_analysis_info):
    for bug_info in manual_analysis_info:
        bug_id2 = bug_info['project'] + "_" + str(bug_info['bugId'])
        if bug_id2.lower() == bug_id:
            return bug_info
    return None

def compare_feature(bug_info1, bug_info2, feature, feature_type):
    automatic_info = bug_info1[feature_type][feature]
    manual_info = feature in bug_info2[feature_type]
    if automatic_info > 0 and manual_info:
        return "Both detected"
    if automatic_info == 0 and not manual_info:
        return "Both didn't detect"
    if automatic_info == 0 and manual_info:
        return "Only manual detected"
    if automatic_info > 0 and not manual_info:
        return "Only automatic detected"

def printf(str, *args):
    print(str % args, end='')

def sorted_alphanumeric(data):
    convert = lambda text: int(text) if text.isdigit() else text.lower()
    alphanum_key = lambda key: [ convert(c) for c in re.split('([0-9]+)', key) ]
    return sorted(data, key=alphanum_key)

path_json1 = sys.argv[1] #Path to the directory with JSON files generated by patch-clustering
path_json2 = sys.argv[2] #Path to the Defects4J dissection JSON file
with open(path_json2) as manual_data:
    manual_analysis_info = json.load(manual_data)
    printf("Bug")
    for feature_type in features.keys():
        for feature in features[feature_type]:
            printf(",%s" % feature)
    print("")
    for json_file_name in sorted_alphanumeric(os.listdir(path_json1)):
        json_file_path = os.path.join(path_json1, json_file_name)
        with open(json_file_path, 'r') as file:
            file_content = json.load(file)
            bug_info = find_info_from_bug(file_content['bugId'], manual_analysis_info)
            printf(file_content['bugId'])
            for feature_type in features.keys():
                for feature in features[feature_type]:
                    printf(",%s" % compare_feature(file_content, bug_info, feature, feature_type))
            print("")
