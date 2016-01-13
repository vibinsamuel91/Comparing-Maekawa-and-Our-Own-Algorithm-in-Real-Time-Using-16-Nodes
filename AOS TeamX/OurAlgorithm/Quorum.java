import java.io.*;
import java.lang.*;
import java.util.*;
import java.net.*;


public class  Quorum {
	ArrayList<ArrayList<Integer>> quorum; 

	public Quorum () {
		quorum = new ArrayList<ArrayList<Integer>> ();
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

	public static ArrayList<String> getQuorumHosts (Quorum quorum, int node) { 
			Quorum q = quorum;			
			int n = node;
			String net;
			ArrayList<String> netxx = new ArrayList<String>();

			int p;
			for (int w=0; w<q.createQuorums(0).get(n).size(); w++) {
				p = w+1;
				if(q.createQuorums(0).get(n).get(w) < 9) {
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
				if(w < 9) {
						net = "net0" + p + ".utdallas.edu";
						all_hosts.add(net);
					} else { 
						net = "net" + p + ".utdallas.edu";
						all_hosts.add(net);						
					  }
			}
			return all_hosts;
	}


	 public int getNodeID (ArrayList<String> arrList, String hostname) { 
		ArrayList<String> hosts = arrList;// new ArrayList<String>;
		String h = hostname;
		int node = 0;
		
		node = hosts.indexOf(h) + 1;
		return node; 	

	}

	public void printQuorums(Quorum quorum, int nodes) {
		Quorum q = quorum;

		System.out.println("\n Quorums: \n");

		/* prints each quorum */
		for(int w=0; w<nodes; w++) {
			System.out.println(q.createQuorums(0).get(w));
		}
	}

	public  ArrayList<Integer> printNodeQuorum (Quorum quorum, int node) {
		Quorum q = quorum;
		int n = node;
		ArrayList<Integer> myQuorum = new ArrayList<Integer>();
		for (int w=0; w<q.createQuorums(0).get(n).size(); w++) {
			System.out.print(q.createQuorums(0).get(n).get(w) + " ");
		}
		return myQuorum;
	}
	public void printSquareGrid(int nodes) {
		
		int n = nodes;
		int k = (int) Math.ceil(Math.sqrt(n));
		int grid [][] = new int [k][k];
		int z = 1;
		for(int x=0; x<k; x++) {
			for(int y=0; y<k; y++) {
				if(z<=n) {
					grid[x][y] = z;
					System.out.printf("%3d ", grid[x][y]);					
					z++;
				}	
			} 
			System.out.println();
		}
	}


}



