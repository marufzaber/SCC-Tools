#!/bin/bash


for i in {1..6}
do
for j in {1..6}
do
for k in {1..6}
do
for l in {1..6}
do
for m in {1..6}
do


rm -rf SourcererCC/clone-detector/sourcerer-cc.properties
javac ConfigGenerator.java
java -cp . ConfigGenerator $i $j $k $l $m

mv sourcerer-cc.properties SourcererCC/clone-detector/
cd SourcererCC/clone-detector/

cp blocks.file input/dataset/
rm -rf input/dataset/oldData/
rm -rf build dist NODE_1 SCC_LOGS gtpmindex fwdindex index
./execute.sh 1
./runnodes.sh init 1
./runnodes.sh index 1
./move-index.sh 1
./runnodes.sh search 1

cd SCC_LOGS/NODE_1/

mv scc.log $i$j$k$l$mscc.log


cd ~


mv SourcererCC/clone-detector/SCC_LOGS/NODE_1/$i$j$k$l$mscc.log ~/ttt/



#javac SCCMonitor.java
#java SCCMonitor $i $j $k $l $m

done
done
done
done
done
