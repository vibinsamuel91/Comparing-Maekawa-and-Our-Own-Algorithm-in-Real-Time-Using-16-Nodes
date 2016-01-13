import java.io.*;
import java.util.*;

public class QuorumAssignment {
	ArrayList<ArrayList<Integer>> quorum; 
	int nodeNum = 0;
	
	public QuorumAssignment (int numNodes) {
		quorum = new ArrayList<ArrayList<Integer>> ();
		nodeNum = numNodes;
	}

	public void createQuorums() {
		int k = (int) Math.ceil(Math.sqrt(nodeNum));
		int m, p, b;
		for(int i=0; i<nodeNum; i++){
			p = i+1;
			quorum.add(new ArrayList<Integer>());
			for(b=0;b<k;b++) {
				if(p%k == 0) {
					for(b=0; b<k; b++) {
						if((p/k) == (b+1)) {
							m = (b*k) + 1;
							for(int c=0; c<k; c++) {
								if(m<=nodeNum) {
									quorum.get(i).add(m);
									m++;
								}
							}
							for(int d=(p-k); d>0; d-=k) {
								quorum.get(i).add(d);
							}
							for(int e=(p+k); e<=nodeNum; e+=k) {
								quorum.get(i).add(e);
							}
						}
					}
				}	
				else {
					if((Math.ceil(p/k) + 1) <=(b+1)){ 
						m = (b*k) + 1;
						for(int c=0; c<k; c++) {
							if(m<=nodeNum) {
									quorum.get(i).add(m);
									m++;
							}
						}
						for(int d=(p-k); d>0; d-=k) {
							quorum.get(i).add(d);
						}
						for(int e=(p+k); e<=nodeNum; e+=k) {
							quorum.get(i).add(e);
						}	
						break;
					}
				}	
			}
		}//end for loop
	}
	
	public ArrayList<ArrayList<Integer>> createQuorums(int numNodes) { 
	
		int k = (int) Math.ceil(Math.sqrt(numNodes));
		int m, p, b;
		for(int i=0; i<numNodes; i++){
			p = i+1;
			quorum.add(new ArrayList<Integer>());
			for(b=0;b<k;b++) {
				if(p%k == 0) {
					for(b=0; b<k; b++) {
						if((p/k) == (b+1)) {
							m = (b*k) + 1;
							for(int c=0; c<k; c++) {
								if(m<=numNodes) {
									quorum.get(i).add(m);
									m++;
								}
							}
							for(int d=(p-k); d>0; d-=k) {
								quorum.get(i).add(d);
							}
							for(int e=(p+k); e<=numNodes; e+=k) {
								quorum.get(i).add(e);
							}
						}
					}
				}	
				else if((Math.ceil(p/k) + 1) <=(b+1)){ 
						m = (b*k) + 1;
						for(int c=0; c<k; c++) {
							if(m<=numNodes) {
									quorum.get(i).add(m);
									m++;
							}
						}
						for(int d=(p-k); d>0; d-=k) {
							quorum.get(i).add(d);
						}
						for(int e=(p+k); e<=numNodes; e+=k) {
							quorum.get(i).add(e);
						}	
						break;
				}
			}
		}
		return quorum;
	}
	
	public static ArrayList<String> getQuorumHosts (QuorumAssignment quorum, int node) { 
		QuorumAssignment q = quorum;			
		int n = node;
		String net;
		ArrayList<String> netxx = new ArrayList<String>();

		int p;
		for (int w=0; w<q.createQuorums(0).get(n).size(); w++) {
			p = w+1;
			if(q.createQuorums(0).get(n).get(w) < 10) {
				net = "net0" + q.createQuorums(0).get(n).get(w) + ".utdallas.edu";
				netxx.add(net);
			} else { 
				net = "net" + q.createQuorums(0).get(n).get(w) + ".utdallas.edu";
				netxx.add(net);						
			}
		}
		return netxx;
	}

	public static ArrayList<String> getAllHosts (int nodes) {
	
		int n = nodes;
		String net; 
		ArrayList<String> all_hosts = new ArrayList<String>();
		int p;
		
		for (int w=0; w<n; w++) {
			p = w+1;
			if(w < 10) {
					net = "net0" + p + ".utdallas.edu";
					all_hosts.add(net);
				} else { 
					net = "net" + p + ".utdallas.edu";
					all_hosts.add(net);						
				  }
		}
		return all_hosts;
	}
	
	//get a node quorum
	public ArrayList<Integer> getQuorum(int quorumNum) {
		return quorum.get(quorumNum);	
	}
	
	//print quorum assignment
	public void printQuorums() {
		//print KxK grid
		int k = (int) Math.ceil(Math.sqrt(nodeNum));
		System.out.println("\n\nK = " + k);
		int grid [][] = new int [k][k];
		int z = 1;
		for(int x=0; x<k; x++) {
			for(int y=0; y<k; y++) {
				if(z<=nodeNum) {
					grid[x][y] = z;
					System.out.printf("%3d ", grid[x][y]);					
					z++;
				}
			}
			System.out.println();
		}
		//print quorums
		for(int i=0; i<nodeNum; i++){
			System.out.print("S[" +i+"]"+":\t");
			System.out.println(quorum.get(i) + "\n");
		}
	}
	
	//print a specific quorum
	public void printAQuorums(int quorumNum) {
		//print quorums
		System.out.print("S[" +quorumNum+"]"+":\t");
		System.out.println(quorum.get(quorumNum) + "\n");
	}
}
