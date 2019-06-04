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
//		System.out.print("�п�J�z�]�w�����e�ȡG");
//		threshold = scanner.nextInt();
		System.out.println("File Name = "+gFilePath);
		long startTime = System.currentTimeMillis();//�����{�b�ɶ�
		LoadDatabase(gFilePath,DB);//���l��Ʃ�iDB(DB�O�G��arraylist<integer>�A����)�̡A�p��Xthreshold�A����headertable
		String threshold_string = "Threshold = "+threshold;
		System.out.println(threshold_string);
		exchangeSort(headertable);//��headertable�̰ӫ~�ƶq�Ѥj��p�ƧǦn
		Delete_row_th_Database(DB);//�NDB�R���S���bheadertable�W���ӫ~
		sortdata(DB);//�NDB�̷�headertable�h���ƧǡA�N���ͪ��sDB��iDB2(�G��arraylist<integer>�A����)
		buildFPtree(DB2,root);//�Q��DB2�h�زĤ@��FPtree
		String ttmp ="";
		TraverseTree_headertablelinktree(root,1,headertable,ttmp);//�Nheadertale��Ĥ@��FPtree�h���쵲���X
		traverseheadertree_produceDB2(headertable,ttmp);//�qheadertable�X�o�h�˱Ԩ��X�A�åB�N���X�����`�I��i�ϰ��ܼƪ�DB_treenode(�G��arraylist<treenode>)
		String fp2 = "Product_combination.txt";
		FileWriter fw = new FileWriter(fp2,true);
		BufferedWriter bfw =new BufferedWriter(fw);
		String totalcount_string = "Totalcount = "+totalcount;//�q�X�`�����ӫ~�զX���ƶq
		String time = "Using Time:" + (System.currentTimeMillis() - startTime) + " ms";//�N�ثe�ɶ���h�}�l�ɶ��A�p��X�g�L�ɶ��C
		
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
			DB.add(t);//��@���Ʃ�iDB�A�`�NDB�O�G��
			for(int i=0; i<s.length; i++)
			{	
				int Item =Integer.parseInt(s[i]);
				t.add(Item);//��@���ƪ��C�@�Ӹ�Ʃ�it�A�`�Nt�O�@��
				gMaxItem = Math.max(gMaxItem,Item);
				gMinItem = Math.min(gMinItem,Item);
				gCountFI[Item]++;//�Y1��120�ӡB50��200�ӡA����gCountFI[1]=120�BgCountFI[50]=200
			}
			totalcount++;//�����`�@���X�����(�`�N �O�@��@�檺�ӫ~�զX�A���O�@�Ӥ@�Ӫ��ӧO�ӫ~)
		}
		threshold = totalcount * threshold / 100;//���`�ӫ~���ƥh���W���e�Ȫ�%�ơA��X���e��
		totalcount=0;//�̫�n�Υ��Ӻ��`�����ӫ~�զX�ƶq
		
		fr.close();
		bfr.close();
		//�⦳�W�Lthreshold��gCountFI��iheadertable
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
				{DB.get(i).remove(DB.get(i).get(j));j--;}	//�R���S���bheadertable�W���ӫ~
			}
		}
	}
	public static void sortdata(ArrayList<ArrayList<Integer>> DB) throws IOException
	{
		int count = 0;boolean ff=false;
		for(int i=0;i<DB.size();i++)
		{
			ArrayList<Integer> t=new ArrayList<Integer>();
			DB2.add(t);//��@���Ʃ�iDB2�A�`�NDB2�O�G��
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
						if(t.child.size() ==0)//�p�Groot�U���S���� �Ω�@�}�l
						{
							treenode tmp = new treenode(DB.get(i).get(j),1);
							t.child.add(tmp);tmp.parent=t;
		 					t = tmp;
		 					equl = true;
						}
						else//�p�G��e�`�I��child�A�h�n�h�@�Ӥ@�ӱ��C��child�h�ݦ��S���@�˪��A���@�˴N�[�_�ӡA�S���N�t�~�s�Wchild
						{
							int r=0;
							boolean ttt = false;
							while(ttt==false&&r<t.child.size())
							{
								if(t.child.get(r).name==DB.get(i).get(j))//���@�˪�child�A�[�_��
								{   t.child.get(r).count++;//�[�_��
		 	 	 					t = t.child.get(r);//�O�o���e�`�I��s�챽�쪺�`�I
		 	 	 					equl = true;ttt = true;
		 	 	 				}
								r++;
							}
								
							if(ttt==false)//������e�`�I���Ҧ�child���S���@�˪��A����N�b��e�`�I�s�Wchild
							{
								treenode tmp = new treenode(DB.get(i).get(j),1);
		 						t.child.add(tmp);tmp.parent=t;//�O�o�Nchild�s�Wparent�A�i�D�����`�I�O�֡A�˱Ԩ��X�n��
		 	 					t = tmp;//�O�o���e�`�I��s�챽�쪺�`�I
		 	 					equl = true;
							}
						} 
					}
					else if(DB.get(i).get(j)!=headertable.get(count).name){count++;}//�p�G���쪺��Ƥ��Oheadertable�ثe���A����N��headertable���U�@�ӭ�
					if(equl==true) {break;}//����Ӹ`�I�[�i�hFPtree�A��M�Nbreak�X�h�A���U�@�Ӹ���o
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
     	  {x=x.nextnode;}//�p�G�U�@�Ӧ��ȴN����U�@��
          if(p.get(count).name==n.name)
          {x.nextnode=n;gh=true;}//��headertable�s��FPtree
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
    		String roottmp;//�O���ڸ`�I
    		if(stringtmp=="")
    		{roottmp = String.valueOf(p.get(i).name);}
    		else
    		{roottmp = p.get(i).name+","+stringtmp;}
    		
    		//����Product_combination.txt
    		String fp2 = "Product_combination.txt";
    		FileWriter fw = new FileWriter(fp2,true);
    		BufferedWriter bfw =new BufferedWriter(fw);
    		String Product_combination ="�����ӫ~�զX: ["+roottmp+"],�ƶq��:"+p.get(i).count;
    		bfw.write(Product_combination);bfw.newLine();
    		bfw.flush();
    		System.out.println();System.out.println();
    		System.out.println(Product_combination);
    		
    		totalcount++;//�����`�@���h�֬����ӫ~�զX
    		ArrayList<ArrayList<treenode>> DB_treenode = new ArrayList<ArrayList<treenode>>();//�O���˱Ԩ��X�����`�I�A�`�N�O�G��ArrayList<treenode>
        	boolean h = false;int firstcount = 0;
        	x = p.get(i);
        	treenode root_v2 = new treenode (0,0);
        	root_v2 = x;
        	System.out.print(p.get(i).name+","+p.get(i).count+"���˱Ԩ��X��");
        	System.out.println();
        	while(x.nextnode!=null&&x!=x.nextnode)
        	{
        		x =x.nextnode;
        		System.out.print("��["+x.name+","+x.count+"]");
        		ArrayList<treenode> tmp_treenode = new ArrayList<treenode>();
        		DB_treenode.add(tmp_treenode);//�]���O�G���� �A�ҥH�n���Ф@�Ӥ@������i�h���G���A�U�豽�쪺���`�I�A��i�o�Ӥ@��tmp_treenode
        		treenode tmp_tree = x;
        		firstcount = x.count;
        		x=x.parent;
        		while(x.parent!=null)
        		{
        			if(h==false)
        			{
        				x.count = firstcount;//�n��Ĥ@�ӱ��쪺�ڸ`�I��count���O�_�ӵ�firstcount
        				System.out.print("��["+x.name+","+x.count+"]");
        				treenode wow = new treenode(x.name,x.count);
        				tmp_treenode.add(wow);//�[�iDB_treenode
        				x = x.parent;h=true;
        			}
        			else
        			{
        				treenode wow = new treenode(x.name,firstcount);//�`�N�A����Ҧ������`�I���ƶq���|�O�ڸ`�I����
        				System.out.print(",��["+wow.name+","+wow.count+"]");
        				tmp_treenode.add(wow);//�[�iDB_treenode
        				x = x.parent;
        			}
        		}
        		x = tmp_tree;//�[�n��n��x�^��@�}�l���ڸ`�I
        		
        	}
        	System.out.println();
        	produce_headertable_v2(DB_treenode,root_v2,roottmp);
        	
        }//for(int i=0;i<p.size();i++)���� 
	}	    
    public static void produce_headertable_v2(ArrayList<ArrayList<treenode>> D,treenode r,String stringtmp) throws IOException
	{
    	//D�ODB_treenode
    	ArrayList<treenode> headertable_v2 = new ArrayList<treenode>();//�Ф@��headertable_v2�A�`�N!!!�n�ΰϰ��ܼơA���i�Υ����ܼơA���j�~���|�~��
   	 	boolean e=false;
   	 	for(int i =0;i<D.size();i++)
		{
   	 		for(int j=0;j<D.get(i).size();j++)
   	 		{
	   	 		treenode wow = new treenode(D.get(i).get(j).name,D.get(i).get(j).count);//���ҥH�n����p���ȥᵹwow�A�O���F���n�ʨ�p���ȡC
	   	 		if(e==false)
				{headertable_v2.add(wow);e=true;}//�N�Ĥ@�Ӹ�ƥ��[�iheadertable_v2
			    else
				{
					int cc=0;boolean hh=false;
					while(cc!=headertable_v2.size())
					{
						if(headertable_v2.get(cc).name==wow.name)//���C�@��headertable_v2�A�ݸ�Ʀ��S����headertable_v2�@��
						{headertable_v2.get(cc).count= headertable_v2.get(cc).count + wow.count;hh=true;}//�@�˷�M�N�[�_��
						cc++;
					}
					if(hh==false) 
					{headertable_v2.add(wow);}//���@�˷�M�N�s�W�bheadertable_v2
				}
   	 		}
		}
   	 	for(int i=0;i<headertable_v2.size();i++)//��C��threshold��headertable_v2�R��
	 	{
	 		if(headertable_v2.get(i).count<threshold)
	 		{headertable_v2.remove(headertable_v2.get(i));i--;}
	 	}
   	 	exchangeSort(headertable_v2);//�A�Nheadertable_v2�Ѥj��p�Ƨ�
   	 	delete_down_th_Database(headertable_v2,D,r,stringtmp);
	}     
	public static void delete_down_th_Database(ArrayList<treenode> h,ArrayList<ArrayList<treenode>> D,treenode r,String stringtmp) throws IOException
    {
    	//h�Oheadertable_v2�AD�ODB_treenode
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
	   	 		{D.get(i).remove(D.get(i).get(j));j--;}//�N�S���bheadertable_v2�W����ƧR��
   	 		}
   	 	}
   	 	sortdata_treenode(D,h,r,stringtmp);
    }
	public static void sortdata_treenode(ArrayList<ArrayList<treenode>> D,ArrayList<treenode> h,treenode r,String stringtmp) throws IOException
	{
		int count = 0;boolean ff=false;
		ArrayList<ArrayList<treenode>> DB2_treenode=new ArrayList<ArrayList<treenode>>();//�Ыؤ@�Ӥ@�ˬO�G����DB2_treenode�A��ƧǹL��DB2_treenode
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
    					if(r.child.size() ==0)//�p�Groot�U���S���� �Ω�@�}�l
    					{
    						r.child.add(tmp2);tmp2.parent=r;
    	 					r = tmp2;
    	 					equl = true;
    					}
    					else//�p�G��e�`�I��child�A�h�n�h�@�Ӥ@�ӱ��C��child�h�ݦ��S���@�˪��A���@�˴N�[�_�ӡA�S���N�t�~�s�Wchild
    					{
    						int cr=0;
    						boolean ttt = false;
    						while(ttt==false&&cr<r.child.size())
    						{
    							if(r.child.get(cr).name==tmp2.name)//���@�˪�child�A�[�_��
    							{   
    								r.child.get(cr).count=r.child.get(cr).count+tmp2.count;
    	 	 	 					r = r.child.get(cr);
    	 	 	 					equl = true;ttt = true;
    	 	 	 				}
    							cr++;
    						}
    						if(ttt==false)//����������child,�S���@�˪��A��M�N�s�Wchild
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
    	//System.out.println("�H�U����");
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
