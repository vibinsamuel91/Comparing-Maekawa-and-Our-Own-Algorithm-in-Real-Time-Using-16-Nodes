#!/usr/bin/expect -f
set USERNAME "jad082000"
set hostList {net01.utdallas.edu net02.utdallas.edu net03.utdallas.edu net04.utdallas.edu net05.utdallas.edu net06.utdallas.edu net07.utdallas.edu net08.utdallas.edu net09.utdallas.edu net10.utdallas.edu net11.utdallas.edu net12.utdallas.edu net13.utdallas.edu net14.utdallas.edu net15.utdallas.edu net16.utdallas.edu}
#puts $hostList
foreach HOST $hostList {
spawn gnome-terminal -e "ssh $USERNAME@$HOST"
interact
}