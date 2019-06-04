import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class finalproject_2 
{
	public static int totalcount;
	public static float threshold=80;
	//public static String gFilePath = "BMS1_itemset_mining.txt";
	public static String gFilePath = "mushrooms.txt";
	//public static String gFilePath = "T10I4D100K.txt";
	public static int gMaxItem = Integer.MIN_VALUE;
	public static int gMinItem = Integer.MAX_VALUE;
	public static int gCountFI[] = new int[100000];
	public static ArrayList<Integer> FIlist = new ArrayList<Integer>();
	public static ArrayList<ArrayList<Integer>> DB = new ArrayList<ArrayList<Integer>>();
	public static ArrayList<ArrayList<Integer>> DB2 = new ArrayList<ArrayList<Integer>>();
	public static ArrayList<treenode> headertable = new ArrayList<treenode>();
	public static treenode root = new treenode (0,0);
	
	public static void main(String[] args) throws IOException 
	{
//		Scanner scanner = new Scanner(System.in);
//		System.out.print("請輸入您設定的門檻值：");
//		threshold = scanner.nextInt();
		System.out.println("File Name = "+gFilePath);
		long startTime = System.currentTimeMillis();//紀錄現在時間
		LoadDatabase(gFilePath,DB);//把原始資料放進DB(DB是二維arraylist<integer>，全域)裡，計算出threshold，產生headertable
		String threshold_string = "Threshold = "+threshold;
		System.out.println(threshold_string);
		exchangeSort(headertable);//把headertable依商品數量由大到小排序好
		Delete_row_th_Database(DB);//將DB刪除沒有在headertable上的商品
		sortdata(DB);//將DB依照headertable去做排序，將產生的新DB放進DB2(二維arraylist<integer>，全域)
		buildFPtree(DB2,root);//利用DB2去建第一顆FPtree
		String ttmp ="";
		TraverseTree_headertablelinktree(root,1,headertable,ttmp);//將headertale跟第一顆FPtree去做鏈結走訪
		traverseheadertree_produceDB2(headertable,ttmp);//從headertable出發去倒敘走訪，並且將走訪的父節點放進區域變數的DB_treenode(二維arraylist<treenode>)
		String fp2 = "Product_combination.txt";
		FileWriter fw = new FileWriter(fp2,true);
		BufferedWriter bfw =new BufferedWriter(fw);
		String totalcount_string = "Totalcount = "+totalcount;//秀出總相關商品組合的數量
		String time = "Using Time:" + (System.currentTimeMillis() - startTime) + " ms";//將目前時間減去開始時間，計算出經過時間。
		
		System.out.println();
		System.out.println(totalcount_string);
		System.out.println(time);
		bfw.write(threshold_string);bfw.newLine();
		bfw.write(totalcount_string);bfw.newLine();
		bfw.write(time);bfw.newLine();
		bfw.flush();
		bfw.close();
		fw.close();
		
	}

	public static void LoadDatabase(String FilePath, ArrayList<ArrayList<Integer>> DB) throws IOException
	 {
		FileReader fr = new FileReader(FilePath);
		BufferedReader bfr = new BufferedReader(fr);
		String tran = "";
		while((tran=bfr.readLine())!=null)
		{
			String s[]=tran.split(" ");
			ArrayList<Integer> t=new ArrayList<Integer>();
			DB.add(t);//把一行資料放進DB，注意DB是二維
			for(int i=0; i<s.length; i++)
			{	
				int Item =Integer.parseInt(s[i]);
				t.add(Item);//把一行資料的每一個資料放進t，注意t是一維
				gMaxItem = Math.max(gMaxItem,Item);
				gMinItem = Math.min(gMinItem,Item);
				gCountFI[Item]++;//若1有120個、50有200個，那麼gCountFI[1]=120、gCountFI[50]=200
			}
			totalcount++;//紀錄總共有幾筆資料(注意 是一行一行的商品組合，不是一個一個的個別商品)
		}
		threshold = totalcount * threshold / 100;//用總商品筆數去乘上門檻值的%數，算出門檻值
		totalcount=0;//最後要用它來算總相關商品組合數量
		
		fr.close();
		bfr.close();
		//把有超過threshold的gCountFI放進headertable
		for(int i=0;i<gCountFI.length;i++)
	    {	
			if(gCountFI[i]>=threshold)			
			{
				treenode tmp = new treenode(i,gCountFI[i]);
				headertable.add(tmp);
			}	
		}
	 }
	public static void exchangeSort(ArrayList<treenode> p)
	 {
		ArrayList<treenode> Ptr = p;
		for(int i=0;i<Ptr.size()-1;i++)
		{ 
			for(int j=0;j<Ptr.size()-i-1;j++)
			{
				 if( Ptr.get(j+1).count > Ptr.get(j).count )
				 { 
					 treenode tmp = new treenode();
					 tmp.count = Ptr.get(j+1).count;
					 tmp.name = Ptr.get(j+1).name;
					 Ptr.get(j+1).count = Ptr.get(j).count;
					 Ptr.get(j+1).name = Ptr.get(j).name;
					 Ptr.get(j).count=tmp.count;
					 Ptr.get(j).name=tmp.name;
				 }
			}
		}
		
	 }
	public static void Delete_row_th_Database(ArrayList<ArrayList<Integer>> DB) throws IOException
	{
		for(int i=0;i<DB.size();i++)
		{   
			for(int j=0;j<DB.get(i).size();j++)
			{
				boolean f = false;int k=0;
				while(k<headertable.size()&&f==false)
				{
					if(headertable.get(k).name==DB.get(i).get(j))
					{f=true;}
					k++;
				}
				if(f==false)
				{DB.get(i).remove(DB.get(i).get(j));j--;}	//刪除沒有在headertable上的商品
			}
		}
	}
	public static void sortdata(ArrayList<ArrayList<Integer>> DB) throws IOException
	{
		int count = 0;boolean ff=false;
		for(int i=0;i<DB.size();i++)
		{
			ArrayList<Integer> t=new ArrayList<Integer>();
			DB2.add(t);//把一行資料放進DB2，注意DB2是二維
			int th = 0;
			int tablecc=0;
			while(th!=DB.get(i).size())
			{
				while(ff==false)
				{
					if(DB.get(i).get(count)==headertable.get(tablecc).name)
					{t.add(DB.get(i).get(count));ff=true;th++;}
					count++;
					if(count == DB.get(i).size()&&ff==false)
					{tablecc++;count=0;}
				}ff=false;tablecc++;count=0;
			}
		}
	}
    public static void buildFPtree(ArrayList<ArrayList<Integer>> DB,treenode t)
    {
	   	boolean equl = false;
	   	for(int i=0;i<DB.size();i++)
		{
	   		t = root;
	   		for(int j=0;j<DB.get(i).size();j++)
			{
	   			equl=false;int count=0;
	   			while(equl==false)
				{
	   				if(DB.get(i).get(j)==headertable.get(count).name)
					{
						if(t.child.size() ==0)//如果root下面沒有樹 用於一開始
						{
							treenode tmp = new treenode(DB.get(i).get(j),1);
							t.child.add(tmp);tmp.parent=t;
		 					t = tmp;
		 					equl = true;
						}
						else//如果當前節點有child，則要去一個一個掃每個child去看有沒有一樣的，有一樣就加起來，沒有就另外新增child
						{
							int r=0;
							boolean ttt = false;
							while(ttt==false&&r<t.child.size())
							{
								if(t.child.get(r).name==DB.get(i).get(j))//有一樣的child，加起來
								{   t.child.get(r).count++;//加起來
		 	 	 					t = t.child.get(r);//記得把當前節點更新到掃到的節點
		 	 	 					equl = true;ttt = true;
		 	 	 				}
								r++;
							}
								
							if(ttt==false)//掃完當前節點的所有child都沒有一樣的，那麼就在當前節點新增child
							{
								treenode tmp = new treenode(DB.get(i).get(j),1);
		 						t.child.add(tmp);tmp.parent=t;//記得將child連上parent，告訴它父節點是誰，倒敘走訪要用
		 	 					t = tmp;//記得把當前節點更新到掃到的節點
		 	 					equl = true;
							}
						} 
					}
					else if(DB.get(i).get(j)!=headertable.get(count).name){count++;}//如果掃到的資料不是headertable目前的，那麼就掃headertable的下一個值
					if(equl==true) {break;}//有把該節點加進去FPtree，當然就break出去，掃下一個資料囉
				} 				
			}
		}
    }
    public static void TraverseTree_headertablelinktree(treenode n, int depth,ArrayList<treenode> p,String stringtmp) throws IOException
    {
    	System.out.print("["+n.name+","+n.count+"]"); 
    	
    	treenode x = new treenode(0,0);
    	boolean gh = false;int count=0;
    	while(gh==false&&count<p.size())
    	{	
          x=p.get(count);
          while(x.nextnode!=null)
     	  {x=x.nextnode;}//如果下一個有值就移到下一個
          if(p.get(count).name==n.name)
          {x.nextnode=n;gh=true;}//把headertable連到FPtree
          count++;
    	}	
    	depth++;
    	for(int i=0; i<n.child.size(); i++)
    	{	
    		System.out.println("");
    		for(int j=0; j<depth; j++)
    		{
    			System.out.print(" ");
    		}
    		System.out.print("|-->");
    		TraverseTree_headertablelinktree(n.child.get(i), depth,p,stringtmp);
    	}
    }
    public static void traverseheadertree_produceDB2(ArrayList<treenode> p,String stringtmp) throws IOException
    {
    	treenode x = new treenode(0,0);
    	for(int i=0;i<p.size();i++)
        {
    		String roottmp;//記錄根節點
    		if(stringtmp=="")
    		{roottmp = String.valueOf(p.get(i).name);}
    		else
    		{roottmp = p.get(i).name+","+stringtmp;}
    		
    		//產生Product_combination.txt
    		String fp2 = "Product_combination.txt";
    		FileWriter fw = new FileWriter(fp2,true);
    		BufferedWriter bfw =new BufferedWriter(fw);
    		String Product_combination ="相關商品組合: ["+roottmp+"],數量為:"+p.get(i).count;
    		bfw.write(Product_combination);bfw.newLine();
    		bfw.flush();
    		System.out.println();System.out.println();
    		System.out.println(Product_combination);
    		
    		totalcount++;//紀錄總共有多少相關商品組合
    		ArrayList<ArrayList<treenode>> DB_treenode = new ArrayList<ArrayList<treenode>>();//記錄倒敘走訪的父節點，注意是二維ArrayList<treenode>
        	boolean h = false;int firstcount = 0;
        	x = p.get(i);
        	treenode root_v2 = new treenode (0,0);
        	root_v2 = x;
        	System.out.print(p.get(i).name+","+p.get(i).count+"的倒敘走訪為");
        	System.out.println();
        	while(x.nextnode!=null&&x!=x.nextnode)
        	{
        		x =x.nextnode;
        		System.out.print("根["+x.name+","+x.count+"]");
        		ArrayList<treenode> tmp_treenode = new ArrayList<treenode>();
        		DB_treenode.add(tmp_treenode);//因為是二維的 ，所以要先創一個一維的放進去給二維，下方掃到的父節點再放進這個一維tmp_treenode
        		treenode tmp_tree = x;
        		firstcount = x.count;
        		x=x.parent;
        		while(x.parent!=null)
        		{
        			if(h==false)
        			{
        				x.count = firstcount;//要把第一個掃到的根節點的count先記起來給firstcount
        				System.out.print("父["+x.name+","+x.count+"]");
        				treenode wow = new treenode(x.name,x.count);
        				tmp_treenode.add(wow);//加進DB_treenode
        				x = x.parent;h=true;
        			}
        			else
        			{
        				treenode wow = new treenode(x.name,firstcount);//注意，之後所有的父節點的數量都會是根節點的值
        				System.out.print(",父["+wow.name+","+wow.count+"]");
        				tmp_treenode.add(wow);//加進DB_treenode
        				x = x.parent;
        			}
        		}
        		x = tmp_tree;//加好後要把x回到一開始的根節點
        		
        	}
        	System.out.println();
        	produce_headertable_v2(DB_treenode,root_v2,roottmp);
        	
        }//for(int i=0;i<p.size();i++)尾部 
	}	    
    public static void produce_headertable_v2(ArrayList<ArrayList<treenode>> D,treenode r,String stringtmp) throws IOException
	{
    	//D是DB_treenode
    	ArrayList<treenode> headertable_v2 = new ArrayList<treenode>();//創一個headertable_v2，注意!!!要用區域變數，不可用全域變數，遞迴才不會洗掉
   	 	boolean e=false;
   	 	for(int i =0;i<D.size();i++)
		{
   	 		for(int j=0;j<D.get(i).size();j++)
   	 		{
	   	 		treenode wow = new treenode(D.get(i).get(j).name,D.get(i).get(j).count);//之所以要先把p的值丟給wow，是為了不要動到p的值。
	   	 		if(e==false)
				{headertable_v2.add(wow);e=true;}//將第一個資料先加進headertable_v2
			    else
				{
					int cc=0;boolean hh=false;
					while(cc!=headertable_v2.size())
					{
						if(headertable_v2.get(cc).name==wow.name)//掃每一個headertable_v2，看資料有沒有跟headertable_v2一樣
						{headertable_v2.get(cc).count= headertable_v2.get(cc).count + wow.count;hh=true;}//一樣當然就加起來
						cc++;
					}
					if(hh==false) 
					{headertable_v2.add(wow);}//不一樣當然就新增在headertable_v2
				}
   	 		}
		}
   	 	for(int i=0;i<headertable_v2.size();i++)//把低於threshold的headertable_v2刪掉
	 	{
	 		if(headertable_v2.get(i).count<threshold)
	 		{headertable_v2.remove(headertable_v2.get(i));i--;}
	 	}
   	 	exchangeSort(headertable_v2);//再將headertable_v2由大到小排序
   	 	delete_down_th_Database(headertable_v2,D,r,stringtmp);
	}     
	public static void delete_down_th_Database(ArrayList<treenode> h,ArrayList<ArrayList<treenode>> D,treenode r,String stringtmp) throws IOException
    {
    	//h是headertable_v2，D是DB_treenode
   	 	for(int i=0;i<D.size();i++)
   	 	{
   	 		for(int j=0;j<D.get(i).size();j++)
   	 		{
	   	 		boolean e=false;int count=0;
	   	 		while(e==false&&count<h.size())
	   	 		{
	   	 			if(D.get(i).get(j).name==h.get(count).name)
	   	 			{ e=true;}
	   	 			count++;
	   	 		}
	   	 		if(e==false)
	   	 		{D.get(i).remove(D.get(i).get(j));j--;}//將沒有在headertable_v2上的資料刪掉
   	 		}
   	 	}
   	 	sortdata_treenode(D,h,r,stringtmp);
    }
	public static void sortdata_treenode(ArrayList<ArrayList<treenode>> D,ArrayList<treenode> h,treenode r,String stringtmp) throws IOException
	{
		int count = 0;boolean ff=false;
		ArrayList<ArrayList<treenode>> DB2_treenode=new ArrayList<ArrayList<treenode>>();//創建一個一樣是二維的DB2_treenode，放排序過的DB2_treenode
		for(int i=0;i<D.size();i++)
		{
			ArrayList<treenode> t=new ArrayList<treenode>();
			DB2_treenode.add(t);
			int th = 0;
			int tablecc=0;
			while(th!=D.get(i).size())
			{
				while(ff==false)
				{
					if(D.get(i).get(count).name==h.get(tablecc).name)
					{t.add(D.get(i).get(count));ff=true;th++;}
					count++;
					if(count == D.get(i).size()&&ff==false)
					{tablecc++;count=0;}
				}
				ff=false;tablecc++;count=0;
			}
		}
		buildFPtree_v2(DB2_treenode,r,h,stringtmp);
	}
    public static void buildFPtree_v2(ArrayList<ArrayList<treenode>> D,treenode r,ArrayList<treenode> h,String stringtmp) throws IOException
    {
    	treenode tmp = r;boolean equl = false;
    	for(int i=0;i<D.size();i++)
		{
    		r=tmp;
    		for(int j=0;j<D.get(i).size();j++)
			{
    			treenode tmp2 = D.get(i).get(j);
        		equl=false;int count=0;
       			while(equl==false)
    			{
       				if(tmp2.name==h.get(count).name)
    				{
    					if(r.child.size() ==0)//如果root下面沒有樹 用於一開始
    					{
    						r.child.add(tmp2);tmp2.parent=r;
    	 					r = tmp2;
    	 					equl = true;
    					}
    					else//如果當前節點有child，則要去一個一個掃每個child去看有沒有一樣的，有一樣就加起來，沒有就另外新增child
    					{
    						int cr=0;
    						boolean ttt = false;
    						while(ttt==false&&cr<r.child.size())
    						{
    							if(r.child.get(cr).name==tmp2.name)//有一樣的child，加起來
    							{   
    								r.child.get(cr).count=r.child.get(cr).count+tmp2.count;
    	 	 	 					r = r.child.get(cr);
    	 	 	 					equl = true;ttt = true;
    	 	 	 				}
    							cr++;
    						}
    						if(ttt==false)//掃完全部的child,沒有一樣的，當然就新增child
    						{
    	 						r.child.add(tmp2);tmp2.parent=r;
    	 	 					r = tmp2;
    	 	 					equl = true;
    						}
    					} 
    				}
    				else if(tmp2.name!=h.get(count).name){count++;}
    				if(equl==true) {break;}
    			} 		
			}
		}
    	//System.out.println("以下為樹");
    	TraverseTree_headertablelinktree(tmp,1,h,stringtmp);
    	traverseheadertree_produceDB2(h,stringtmp);
    }
    public static class treenode
	{
	    int name,count;
	    ArrayList<treenode> child;
	    treenode parent;
	    treenode nextnode;
	    public treenode()
	    {}
	    public treenode(int x,int y)
	    {   
	    	name = x;count = y;nextnode=null;
	    	child = new ArrayList<treenode>();
	    }	
	}
    
}
