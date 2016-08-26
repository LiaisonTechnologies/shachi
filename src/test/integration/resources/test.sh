#!/bin/bash

################################################################################
# MANUAL TESTING SCRIPT                                                        #
# ---------------------------------------------------------------------------- #
# This script is a crutch to use to integration-test Shachi using manual-test  #
# classes (such as com.liaison.shachi.test.e2e.TestMapREnd2End, in particular) #
# manually until such time that those tests are reinvented as a proper (unit   #
# or integration) test suite. To use:                                          #
#                                                                              #
# (1) Build Shachi using the desired profile (e.g. MapR) and the distZip goal  #
#     to build a ZIP distribution with dependencies                            #
# (2) Copy this script, log4j.properties, and the distZip to a location with   #
#     access to HBase.                                                         #
# (3) Adjust the archive name (and version), HBase tables path, log4j          #
#     configuration location, and classpath locations in the following lines   #
#     to reflect the environment.                                              #
# (4) Execute the script.                                                      #
################################################################################

rm -rf hbase-client-0.1.9;
unzip hbase-client-0.1.9.zip;
java -DPATH_MAPRTABLES="/user/mapr/tables/TEMP/BSMITH." -Dlog4j.configuration="file:///home/bsmith/Projects/HBaseClientTest/conf/log4j.properties" -cp /home/bsmith/Projects/HBaseClientTest/hbase-client-0.1.9/hbase-client-0.1.9.jar:/home/bsmith/Projects/HBaseClientTest/hbase-client-0.1.9/lib/* com.liaison.hbase.test.e2e.TestMapREnd2End