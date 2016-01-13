//TeamX
//Jason Daza, Kunal Tripathi, Vibin Daniel, Marilyne Mendolla

/*********
* Maekawa Inputs
*********/
args[] need to be in the form (space-delimited):
  args[0] = node ID
  args[1] = total number of nodes in the system
  args[2] = critical section delay (in seconds)
  args[3] = relative path to config and log files (ex /home/004/m/mx/mxm122230/AOSTeamX/Maekawa/)
  args[4] = config file name
  args[5] = MutEX log file name
  args[6] = stats log file name (ex Node1.txt for Node 1)
  args[7] = sync log file name

/*********
* Config file format
*********/
The configuration file needs to be in the format
  1,5
  where each new line contains a pair of integers, comma-delimited
  first integer (1 in example) is the timestamp
  second integer (5 in example) is the Node ID

/*********
* Required Classes
*********/
Maekawa, Message, ReadWriteHelperClass,
MutexNode, MutexNodeThread, MutexServer, MutexServerThread,
Quorum, QuorumAssignment, ClientServer, SendRequestMsg 

/*********
* Spawn Shells Quickly (Optional)
*   Note: tested on Ubuntu only
*********/
Modify script.sh: change the username. Make sure all of the hostnames have been added
execute: ./script.sh

/******
* Step 1: unzip file in your home directory on net01.utdallas.edu
*******/
unzip AOSTeamX.zip
cd ./AOSTeamX/Maekawa
add the ./AOSTeamX/Log directory if not already created

/*******
* Step 2: Compile Maekawa
*******/
javac Maekawa.java

/**********
* Step 3: Run Maekawa Manually 
*    Note that args[6] is different for each node, ie NodeX.txt
*    Make sure you execute in the maekawa folder
* **IMPORTANT** execute the java command on each node but DON'T HIT ENTER YET (see Step 4)
* Must update the config files for each different test case! config_test1.txt-config_test5.txt already exist
**********/

java Maekawa 1 16 5 ./Log config_test1.txt mutex.txt Node1.txt sync.txt 

java Maekawa 2 16 5 ./Log config_test1.txt mutex.txt Node2.txt sync.txt 

java Maekawa 3 16 5 ./Log config_test1.txt mutex.txt Node3.txt sync.txt 

java Maekawa 4 16 5 ./Log config_test1.txt mutex.txt Node4.txt sync.txt 

java Maekawa 5 16 5 ./Log config_test1.txt mutex.txt Node5.txt sync.txt 

java Maekawa 6 16 5 ./Log config_test1.txt mutex.txt Node6.txt sync.txt 

java Maekawa 7 16 5 ./Log config_test1.txt mutex.txt Node7.txt sync.txt 

java Maekawa 8 16 5 ./Log config_test1.txt mutex.txt Node8.txt sync.txt 

java Maekawa 9 16 5 ./Log config_test1.txt mutex.txt Node9.txt sync.txt 

java Maekawa 10 16 5 ./Log config_test1.txt mutex.txt Node10.txt sync.txt 

java Maekawa 11 16 5 ./Log config_test1.txt mutex.txt Node11.txt sync.txt 

java Maekawa 12 16 5 ./Log config_test1.txt mutex.txt Node12.txt sync.txt 

java Maekawa 13 16 5 ./Log config_test1.txt mutex.txt Node13.txt sync.txt 

java Maekawa 14 16 5 ./Log config_test1.txt mutex.txt Node14.txt sync.txt 

java Maekawa 15 16 5 ./Log config_test1.txt mutex.txt Node15.txt sync.txt 

java Maekawa 16 16 5 ./Log config_test1.txt mutex.txt Node16.txt sync.txt 

/*************
* Step 4: **IMPORTANT**
*    must hit enter in sequential order on the nodes
*    ie hit enter on net01.utdallas.edu first, then net02, then net03,
*    etc, and last on netX (where X = total  number of nodes in system)
**************/

/*************
* Step 5: terminate algorithm
**************/
The algorithm terminates on its own once all the requests have been processed

/*************
* Step 6: Analyze results
**************/
1. Was mutual exclusion violated? Check the mutex.log file in the algorithm directory. Each node writes to this file when
      it enters and when it exits its critical section. 
2. Where all REQUESTs eventually granted? Each node logs to a file each message sent and received. 
      If the naming convention in the examples above (Step 3) is followed, then in the algorithm 
	  directory, each node will have written a NodeX.txt file, where X is the node number. This will show all messages
	  traversing through the system as well as the timestamps for each event or message, for statistical analysis.
