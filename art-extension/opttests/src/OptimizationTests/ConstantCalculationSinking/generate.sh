#!/bin/sh
#
# Copyright (C) 2015 Intel Corporation
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#


# This script generates test cases for combination of int/long and +/-/*/%// based on a test containing methid testLoop() for int/+.
# Generated test cases migh contain errors and should be revised attentively. In particular, test methods for mutiplication should have less 
# number of iterations in order to not overflow. On the other hand, make sure that loop with small number of iterations is not fully unrolled.
# For a moment (December 2014) full unrolling occur if there are < 60 instructions in a loop.

TESTDIR=$1

if [ ! -d $TESTDIR ] ; then
    echo "No such directory: $TEST"
    exit 1
fi


NEWTESTDIR=`echo $TESTDIR | sed "s/AddInt/IntLong/"`
cp -r $TESTDIR $NEWTESTDIR
mv $TESTDIR $TESTDIR.bak

# Rename package
sed "s/$TESTDIR/$NEWTESTDIR/" $NEWTESTDIR/Main.java > $NEWTESTDIR/Main.java.new && mv $NEWTESTDIR/Main.java.new $NEWTESTDIR/Main.java
# Rename testLoop to testLoopAddInt
sed  's/testLoop/testLoopAddInt/g' $NEWTESTDIR/Main.java >  $NEWTESTDIR/Main.java.new && mv $NEWTESTDIR/Main.java.new $NEWTESTDIR/Main.java

# Rename testLoop to testLoopAddInt in postproc
sed s/\'testLoop:[0-9]*\'/\'testLoopAddInt:\1\'/ $NEWTESTDIR/postproc >  $NEWTESTDIR/postproc.new && mv  $NEWTESTDIR/postproc.new  $NEWTESTDIR/postproc
chmod 755 $NEWTESTDIR/postproc

# Find line numbers for testLoop() method beginning and end (beginning of main)
linenumber1=`grep -n "public int testLoopAddInt" $NEWTESTDIR/Main.java | cut -f1 -d: | head  -1`
linenumber2=`grep -n "public static void main" $NEWTESTDIR/Main.java | cut -f1 -d: | head  -1`
number_regexp='^[0-9]+$'
if ! [[ "x$linenumber1" == "x" || "x$linenumber1" == "x" || $linenumber1 =~ $number_regexp || $linenumber2 =~ $number_regexp ]] ; then
    echo "UNEXPECTED: failed to find beginning or end of testLoop function"
    exit 1
fi
linenumber2=$((linenumber2-1))

# Extract testLoopAddInt to a separate file
sed -n ${linenumber1},${linenumber2}p $NEWTESTDIR/Main.java  > $NEWTESTDIR/testLoopAddInt.java

# create testLoopAddLong file
sed "s/int/long/g" $NEWTESTDIR/testLoopAddInt.java | sed "s/Int/Long/g" >  $NEWTESTDIR/testLoopAddLong.java


# Now modify postproc
#sed s/\'testLoop:[0-9]*\'/\'testLoopInt:\1\'\ \'testLoopLong:\1\'/ $NEWTESTDIR/postproc >  $NEWTESTDIR/postproc.new && mv  $NEWTESTDIR/postproc.new  $NEWTESTDIR/postproc
#chmod 755 $NEWTESTDIR/postproc



# Generate based on testLoopAddInt
function GenerateTestForOperation()
{
    oper=$1
    operName=$2
    if [ ! -s $NEWTESTDIR/testLoopAddInt.java ] ; then
        echo "No such directory: $NEWTESTDIR/testLoopAddInt.java"
        exit 1
    fi

    cat $NEWTESTDIR/testLoopAddInt.java | sed "s/testVar +=/testVar $oper=/g"  | sed "s/testLoopAddInt/testLoop${operName}Int/" > ${NEWTESTDIR}/testLoop${operName}Int.java
    cat $NEWTESTDIR/testLoopAddInt.java | sed "s/testVar +=/testVar $oper=/g"  | sed "s/testLoopAddInt/testLoop${operName}Long/" | sed "s/int/long/g" > "${NEWTESTDIR}/testLoop${operName}Long.java" 
}

# Append method file before main method
function InsertMethodAndCall()
{
    operName=$1
    typeName=$2
    filename="testLoop${1}${2}.java"

    if [ ! -s $NEWTESTDIR/$filename ] ; then
        echo "No such file: $NEWTESTDIR/$filename"
        exit 1
    fi

    # Insert marker line before main
    sed '
    /public static void main/ i\
        MARKER_MARKER
    ' $NEWTESTDIR/Main.java >  $NEWTESTDIR/Main.java.new && mv $NEWTESTDIR/Main.java.new $NEWTESTDIR/Main.java

    # Insert the content of testLoopLong instead of marker line
    sed "/MARKER_MARKER/ {
        r $NEWTESTDIR/$filename
        d
    }" $NEWTESTDIR/Main.java > $NEWTESTDIR/Main.java.new && mv $NEWTESTDIR/Main.java.new $NEWTESTDIR/Main.java

    sed "s/\(System.out.println(new Main().testLoop\)\(AddInt\)\(.*\)/\1\2\3\n\1${operName}${typeName}\3/" $NEWTESTDIR/Main.java > $NEWTESTDIR/Main.java.new && mv $NEWTESTDIR/Main.java.new $NEWTESTDIR/Main.java

}

function RefactorPostproc()
{
    operName=$1
    typeName=$2

    sed "s/\(.*\)\(testLoopAddInt:\)\([0-9]*\)\(.*\)\()$\)/\1\2\3\4 \'testLoop${operName}${typeName}:\3\'\5/" $NEWTESTDIR/postproc >  $NEWTESTDIR/postproc.new && mv  $NEWTESTDIR/postproc.new  $NEWTESTDIR/postproc
    chmod 755 $NEWTESTDIR/postproc

}

function CleanTempFiles() 
{
    echo "Cleaning temp files"
    rm $NEWTESTDIR/testLoop*.java
}



GenerateTestForOperation "-" "Sub"
GenerateTestForOperation "*" "Mul"
GenerateTestForOperation "\/" "Div"
GenerateTestForOperation "%" "Rem"
InsertMethodAndCall "Sub" "Int"
InsertMethodAndCall "Sub" "Long"
InsertMethodAndCall "Mul" "Int"
InsertMethodAndCall "Mul" "Long"
InsertMethodAndCall "Div" "Int"
InsertMethodAndCall "Div" "Long"
InsertMethodAndCall "Rem" "Int"
InsertMethodAndCall "Rem" "Long"
InsertMethodAndCall "Add" "Long"

RefactorPostproc  "Add" "Long"
RefactorPostproc  "Sub" "Int"
RefactorPostproc  "Sub" "Long"
RefactorPostproc  "Mul" "Int"
RefactorPostproc  "Mul" "Long"
RefactorPostproc  "Div" "Int"
RefactorPostproc  "Div" "Long"
RefactorPostproc  "Rem" "Int"
RefactorPostproc  "Rem" "Long"

CleanTempFiles
