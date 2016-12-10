import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;

public class Planner {
	Vector operators;
	Random rand;
	Vector plan;
	Vector finalgoal;

	public static void main(String argv[]){
		(new Planner()).start();
	}

	Planner(){
		rand = new Random();
	}
	
	//追加
	public Vector goalsort(Vector goalList){
		Vector newgoal = new Vector();
		ArrayList ontable = new ArrayList();
		ArrayList xony = new ArrayList();
		ArrayList clear = new ArrayList();
		ArrayList hE = new ArrayList();
		ArrayList poada = new ArrayList();
		
		for(int i = 0; i < goalList.size(); ++i){ //ゴールの内容ごとに分類
			StringTokenizer st = new StringTokenizer((String)goalList.elementAt(i));
			String tmp = st.nextToken();
			if(tmp.equals("ontable")){
				ontable.add((String)goalList.elementAt(i));
				poada.add(st.nextToken());
			}else if(tmp.equals("clear")){
				clear.add((String)goalList.elementAt(i));
			}else if(tmp.equals("handEmpty")){
				hE.add((String)goalList.elementAt(i));
			}else{
				xony.add((String)goalList.elementAt(i));
			}
		}
		
		for(int i = 0; i < ontable.size(); ++i){
			newgoal.add(ontable.get(i));
		}
		
		//System.out.println("before:" + xony);
		xony = XonYsort(xony,poada);
		//System.out.println("after:" + xony);
		
		for(int i = 0; i < xony.size(); ++i){
			newgoal.add(xony.get(i));
		}
		for(int i = 0; i < clear.size(); ++i){
			newgoal.add(clear.get(i));
		}
		for(int i = 0; i < hE.size(); ++i){
			newgoal.add(hE.get(i));
		}
		
		return newgoal;
	}
	
	public ArrayList XonYsort(ArrayList xony,ArrayList poada){
		ArrayList newxony = new ArrayList();
		
		for(int i = 0; i < poada.size(); ++i){
			for(int j = 0; j < xony.size(); ++j){
				
				StringTokenizer st = new StringTokenizer((String)xony.get(j));
				String X = st.nextToken();
				st.nextToken();
				String Y = st.nextToken();
				if(poada.get(i).equals(Y)){
					newxony.add((String)xony.get(j));
					poada.set(i, (String)X);
					j = -1;
				}
			}
			
		}
		
		return newxony;
	}
	//ここまで椙田

	public void start(){
		initOperators();
		Vector goalList     = initGoalList();
		Vector initialState = initInitialState();
		
		goalList = goalsort(goalList);
		//System.out.println(goalList);

		Hashtable theBinding = new Hashtable();
		plan = new Vector();
		planning(goalList,initialState,theBinding);

		System.out.println("***** This is a plan! *****");
		for(int i = 0 ; i < plan.size() ; i++){
			Operator op = (Operator)plan.elementAt(i);
			System.out.println((op.instantiate(theBinding)).name);
		}
	}

	private boolean planning(Vector theGoalList,
			Vector theCurrentState,
			Hashtable theBinding){
		System.out.println("*** GOALS ***" + theGoalList);
		if(theGoalList.size() == 1){
			String aGoal = (String)theGoalList.elementAt(0);
			if(planningAGoal(aGoal,theCurrentState,theBinding,0) != -1){
				return true;
			} else {
				return false;
			}
		} else {
			String aGoal = (String)theGoalList.elementAt(0);
			int cPoint = 0;
			while(cPoint < operators.size()){
				//System.out.println("cPoint:"+cPoint);
				// Store original binding
				Hashtable orgBinding = new Hashtable();
				for(Enumeration e = theBinding.keys() ; e.hasMoreElements();){
					String key = (String)e.nextElement();
					String value = (String)theBinding.get(key);
					orgBinding.put(key,value);
				}
				Vector orgState = new Vector();
				for(int i = 0; i < theCurrentState.size() ; i++){
					orgState.addElement(theCurrentState.elementAt(i));
				}

				int tmpPoint = planningAGoal(aGoal,theCurrentState,theBinding,cPoint);
				//System.out.println("tmpPoint: "+tmpPoint);
				if(tmpPoint != -1){
					theGoalList.removeElementAt(0);
					System.out.println(theCurrentState);
					if(planning(theGoalList,theCurrentState,theBinding)){
						//System.out.println("Success !");
						return true;
					} else {
						cPoint = tmpPoint;
						//System.out.println("Fail::"+cPoint);
						theGoalList.insertElementAt(aGoal,0);

						theBinding.clear();
						for(Enumeration e=orgBinding.keys();e.hasMoreElements();){
							String key = (String)e.nextElement();
							String value = (String)orgBinding.get(key);
							theBinding.put(key,value);
						}
						theCurrentState.removeAllElements();
						for(int i = 0 ; i < orgState.size() ; i++){
							theCurrentState.addElement(orgState.elementAt(i));
						}
					}
				} else {
					theBinding.clear();
					for(Enumeration e=orgBinding.keys();e.hasMoreElements();){
						String key = (String)e.nextElement();
						String value = (String)orgBinding.get(key);
						theBinding.put(key,value);
					}
					theCurrentState.removeAllElements();
					for(int i = 0 ; i < orgState.size() ; i++){
						theCurrentState.addElement(orgState.elementAt(i));
					}
					return false;
				}
			}
			return false;
		}
	}

	private int planningAGoal(String theGoal,Vector theCurrentState,
			Hashtable theBinding,int cPoint){
		System.out.println("**"+theGoal);
		int size = theCurrentState.size();
		for(int i =  0; i < size ; i++){
			String aState = (String)theCurrentState.elementAt(i);
			if((new Unifier()).unify(theGoal,aState,theBinding)){
				return 0;
			}
		}
		
		int index = ConflictResolution(theGoal,theCurrentState);
		Operator op = (Operator)operators.elementAt(index);
		operators.removeElementAt(index);
		operators.insertElementAt(op,0);

//		int randInt = Math.abs(rand.nextInt()) % operators.size();
//		Operator op = (Operator)operators.elementAt(randInt);
//		operators.removeElementAt(randInt);
//		operators.addElement(op);

		for(int i = cPoint ; i < operators.size() ; i++){
			Operator anOperator = rename((Operator)operators.elementAt(i));
			// 現在のCurrent state, Binding, planをbackup
			Hashtable orgBinding = new Hashtable();
			for(Enumeration e = theBinding.keys() ; e.hasMoreElements();){
				String key = (String)e.nextElement();
				String value = (String)theBinding.get(key);
				orgBinding.put(key,value);
			}
			Vector orgState = new Vector();
			for(int j = 0; j < theCurrentState.size() ; j++){
				orgState.addElement(theCurrentState.elementAt(j));
			}
			Vector orgPlan = new Vector();
			for(int j = 0; j < plan.size() ; j++){
				orgPlan.addElement(plan.elementAt(j));
			}

			Vector addList = (Vector)anOperator.getAddList();
			for(int j = 0 ; j < addList.size() ; j++){
				if((new Unifier()).unify(theGoal,
						(String)addList.elementAt(j),
						theBinding)){
					Operator newOperator = anOperator.instantiate(theBinding);
					Vector newGoals = (Vector)newOperator.getIfList();
					System.out.println(newOperator.name);
					if(planning(newGoals,theCurrentState,theBinding)){
						//newOperator = newOperator.instantiate(theBinding);
						System.out.println(newOperator.name);
						if(newOperator.applyStatecheck(theCurrentState, theBinding)){
							System.out.println("チェックは正常終了した○○○○");
							plan.addElement(newOperator);
							theCurrentState =
									newOperator.applyState(theCurrentState,theBinding);
							System.out.println(theCurrentState);
							return i+1;
						}else{
							System.out.println("チェックは失敗した××××");
							// 失敗したら元に戻す．

							Vector vars = new Vector();
							String aName = (String)newOperator.name;
							getVars(aName,vars);

							for(int k = 0 ; k < vars.size(); k++){

								theBinding.remove(newOperator);

							}


							/*
							theBinding.clear();
							for(Enumeration e=orgBinding.keys();e.hasMoreElements();){
								String key = (String)e.nextElement();
								String value = (String)orgBinding.get(key);
								theBinding.put(key,value);
							}

							theCurrentState.removeAllElements();
							for(int k = 0 ; k < orgState.size() ; k++){
								theCurrentState.addElement(orgState.elementAt(k));
							}

							plan.removeAllElements();
							for(int k = 0 ; k < orgPlan.size() ; k++){
								plan.addElement(orgPlan.elementAt(k));
							}
*/
						}
					} else {
						// 失敗したら元に戻す．
						System.out.println("###############aghhhhhh################");
						Vector vars = new Vector();
						String aName = (String)newOperator.name;
						getVars(aName,vars);
						for(int k = 0 ; k < vars.size(); k++){

							theBinding.remove(vars.elementAt(k));

						/*
						theBinding.clear();
						for(Enumeration e=orgBinding.keys();e.hasMoreElements();){
							String key = (String)e.nextElement();
							String value = (String)orgBinding.get(key);
							theBinding.put(key,value);
						}
						theCurrentState.removeAllElements();
						for(int k = 0 ; k < orgState.size() ; k++){
							theCurrentState.addElement(orgState.elementAt(k));
*/						}
						plan.removeAllElements();
						for(int k = 0 ; k < orgPlan.size() ; k++){
							plan.addElement(orgPlan.elementAt(k));

						}
					}
				}
			}
		}
		return -1;
	}

	
	/**
	 * 追加内容
	 * 競合解消するため、現在の状態と実行に必要な状態がもっとも一致するものを選択
	 */
	int ConflictResolution(String theGoal,Vector theCurrentState){
		String[] goal = theGoal.split(" ",0);
		int[] cost = new int[operators.size()];
		boolean mflag;
		for(int i=0;i<operators.size();++i){
			Operator anOperator = (Operator)operators.elementAt(i);
			Vector addlist = (Vector)anOperator.getAddList();
			Vector iflist = (Vector)anOperator.getIfList();
			Vector addList = new Vector();
			for(int j=0;j<addlist.size();++j){
				addList.addElement(((String)addlist.elementAt(j)));
			}
			Vector ifList = new Vector();
			for(int j=0;j<iflist.size();++j){
				ifList.addElement(((String)iflist.elementAt(j)));
			}
			mflag = false;
			for(int j=0;j<addList.size();++j){
				String[] str = ((String)addList.elementAt(j)).split(" ",0);
				if(str.length==goal.length){
					if(goal.length==3){
						for(int k=0;k<ifList.size();++k){
							String[] s = ((String)ifList.elementAt(k)).split(" ",0);
							if(s[1].equals("?x")){
								s[1] = goal[0];
							}else if(s[1].equals("?y")){
								s[1] = goal[2];
							}
							StringBuffer buf = new StringBuffer();	
							buf.append(s[0]);
							for(int l=1;l<s.length;++l)buf.append(" "+s[l]);
							ifList.set(k,buf.toString());
						}
						mflag = true;
					}else if(goal.length==2){
						if(goal[0].equals(str[0])){
							for(int k=0;k<ifList.size();++k){
								String[] s = ((String)ifList.elementAt(k)).split(" ",0);
								if(s.length==3){
									if(str[1]=="?x") s[0]=goal[1];
									else s[2] = goal[1];
								}else if(s.length==2){
									if(s[1].equals(str[1])) s[1]=goal[1];
								}
								StringBuffer buf = new StringBuffer();	
								buf.append(s[0]);
								for(int l=1;l<s.length;++l)buf.append(" "+s[l]);
								ifList.set(k,buf.toString());
							}
							mflag=true;
						}
					}else if(goal.length==1){mflag=true;}
				}
			}

			Hashtable bind = new Hashtable();
			if(mflag){
				for(int j = 0 ; j < ifList.size() ; ++j){
					int size = theCurrentState.size();
					for(int k=0;k<size;++k){
						String aState = (String)theCurrentState.elementAt(k);
						if((new Unifier()).unify((String)ifList.elementAt(j),aState,bind)){
							cost[i]+=1;
						}
					}
				}
				bind.clear();
			}
		}

		int index = 0;
		int max=cost[0];
		for(int i=1;i<cost.length;++i){
			if(cost[i]>=max){
				index = i;
				max = cost[i];
			}
		}
		if(goal[0].equals("handEmpty")){
			String[] hold = ((String)theCurrentState.lastElement()).split(" ",0);
			for(int i=0;i<finalgoal.size();++i){
				String[] fg = ((String)finalgoal.elementAt(i)).split(" ",0);
				if(fg.length==3){
					if(fg[0]==hold[1] && theCurrentState.contains("clear "+fg[2])){
						return index;
					}
				}
			}
			cost[index] -= 5;
			index = 0;
			max=cost[0];
			for(int i=1;i<cost.length;++i){
				if(cost[i]>=max){
					index = i;
					max = cost[i];
				}
			}
		}
		
		return index;
	}
	//ここまで

	private Vector getVars(String thePattern,Vector vars){
		StringTokenizer st = new StringTokenizer(thePattern);
		for(int i = 0 ; i < st.countTokens();){
			String tmp = st.nextToken();
			if(var(tmp)){
				vars.addElement(tmp);
			}
		}
		return vars;
	}
	private boolean var(String str1){
		// 先頭が ? なら変数
		return str1.startsWith("?");
	}

	int uniqueNum = 0;
	private Operator rename(Operator theOperator){
		Operator newOperator = theOperator.getRenamedOperator(uniqueNum);
		uniqueNum = uniqueNum + 1;
		return newOperator;
	}

	private Vector initGoalList(){
		Vector goalList = new Vector();
		goalList.addElement("F on G");
		//goalList.addElement("ontable C");
		goalList.addElement("clear A");
		goalList.addElement("A on B");
		goalList.addElement("ontable E");
		goalList.addElement("B on C"); //(下に積む順番にする)
		//goalList.addElement("A on B");
		goalList.addElement("D on E");
		
		goalList.addElement("ontable C"); //ゴールの順番大事(?x on ?yより前)
		//goalList.addElement("clear A"); //(?x on ?yの後ろ)
		goalList.addElement("handEmpty"); //(最後)
		goalList.addElement("ontable G");
		finalgoal = (Vector)goalList.clone();
		return goalList;
	}

	private Vector initInitialState(){
		Vector initialState = new Vector();
		//initialState.addElement("clear A");
		initialState.addElement("clear B");
		//initialState.addElement("clear C");
		initialState.addElement("clear G");
		
		initialState.addElement("ontable A");
		//initialState.addElement("ontable B");
		//initialState.addElement("ontable C");
		initialState.addElement("ontable D");
		
		initialState.addElement("B on C");
		initialState.addElement("C on A");
		//initialState.addElement("D on B");
		initialState.addElement("E on D");
		initialState.addElement("F on E");
		initialState.addElement("G on F");
		initialState.addElement("handEmpty");
		return initialState;
	}

	private void initOperators(){
		operators = new Vector();

		// OPERATOR 1
		/// NAME
		String name1 = new String("Place ?x on ?y");
		/// IF
		Vector ifList1 = new Vector();
		ifList1.addElement(new String("clear ?y"));
		ifList1.addElement(new String("holding ?x"));
		/// ADD-LIST
		Vector addList1 = new Vector();
		addList1.addElement(new String("?x on ?y"));
		addList1.addElement(new String("clear ?x"));
		addList1.addElement(new String("handEmpty"));
		/// DELETE-LIST
		Vector deleteList1 = new Vector();
		deleteList1.addElement(new String("clear ?y"));
		deleteList1.addElement(new String("holding ?x"));
		Operator operator1 =
				new Operator(name1,ifList1,addList1,deleteList1);
		operators.addElement(operator1);

		// OPERATOR 2
		/// NAME
		String name2 = new String("remove ?x from on top ?y");
		/// IF
		Vector ifList2 = new Vector();
		ifList2.addElement(new String("?x on ?y"));
		ifList2.addElement(new String("clear ?x"));
		ifList2.addElement(new String("handEmpty"));
		/// ADD-LIST
		Vector addList2 = new Vector();
		addList2.addElement(new String("clear ?y"));
		addList2.addElement(new String("holding ?x"));
		/// DELETE-LIST
		Vector deleteList2 = new Vector();
		deleteList2.addElement(new String("?x on ?y"));
		deleteList2.addElement(new String("clear ?x"));
		deleteList2.addElement(new String("handEmpty"));
		Operator operator2 =
				new Operator(name2,ifList2,addList2,deleteList2);
		operators.addElement(operator2);

		// OPERATOR 3
		/// NAME
		String name3 = new String("pick up ?x from the table");
		/// IF
		Vector ifList3 = new Vector();
		ifList3.addElement(new String("ontable ?x"));
		ifList3.addElement(new String("clear ?x"));
		ifList3.addElement(new String("handEmpty"));
		/// ADD-LIST
		Vector addList3 = new Vector();
		addList3.addElement(new String("holding ?x"));
		/// DELETE-LIST
		Vector deleteList3 = new Vector();
		deleteList3.addElement(new String("ontable ?x"));
		deleteList3.addElement(new String("clear ?x"));
		deleteList3.addElement(new String("handEmpty"));
		Operator operator3 =
				new Operator(name3,ifList3,addList3,deleteList3);
		operators.addElement(operator3);

		// OPERATOR 4
		/// NAME
		String name4 = new String("put ?x down on the table");
		/// IF
		Vector ifList4 = new Vector();
		ifList4.addElement(new String("holding ?x"));
		/// ADD-LIST
		Vector addList4 = new Vector();
		addList4.addElement(new String("ontable ?x"));
		addList4.addElement(new String("clear ?x"));
		addList4.addElement(new String("handEmpty"));
		/// DELETE-LIST
		Vector deleteList4 = new Vector();
		deleteList4.addElement(new String("holding ?x"));
		Operator operator4 =
				new Operator(name4,ifList4,addList4,deleteList4);
		operators.addElement(operator4);
	}
}

class Operator{
	String name;
	Vector ifList;
	Vector addList;
	Vector deleteList;

	Operator(String theName,
			Vector theIfList,Vector theAddList,Vector theDeleteList){
		name       = theName;
		ifList     = theIfList;
		addList    = theAddList;
		deleteList = theDeleteList;
	}

	public Vector getAddList(){
		return addList;
	}

	public Vector getDeleteList(){
		return deleteList;
	}

	public Vector getIfList(){
		return ifList;
	}

	public String toString(){
		String result =
				"NAME: "+name + "\n" +
						"IF :"+ifList + "\n" +
						"ADD:"+addList + "\n" +
						"DELETE:"+deleteList;
		return result;
	}

	public boolean applyStatecheck(Vector theState,Hashtable theBinding){
		Vector checkState= (Vector)theState.clone();
		for(int i = 0 ; i < addList.size() ; i++){
			if(checkState.contains(instantiateString((String)addList.elementAt(i),theBinding)))return false;
		}
		for(int i = 0 ; i < deleteList.size() ; i++){
			if(!checkState.contains(instantiateString((String)deleteList.elementAt(i),theBinding)))return false;
		}
		return true;
	}


	public Vector applyState(Vector theState,Hashtable theBinding){
		for(int i = 0 ; i < addList.size() ; i++){
			theState.addElement(instantiateString((String)addList.elementAt(i),theBinding));
		}
		for(int i = 0 ; i < deleteList.size() ; i++){
			theState.removeElement(instantiateString((String)deleteList.elementAt(i),theBinding));
		}
		return theState;
	}


	public Operator getRenamedOperator(int uniqueNum){
		Vector vars = new Vector();
		// IfListの変数を集める
		for(int i = 0 ; i < ifList.size() ; i++){
			String anIf = (String)ifList.elementAt(i);
			vars = getVars(anIf,vars);
		}
		// addListの変数を集める
		for(int i = 0 ; i < addList.size() ; i++){
			String anAdd = (String)addList.elementAt(i);
			vars = getVars(anAdd,vars);
		}
		// deleteListの変数を集める
		for(int i = 0 ; i < deleteList.size() ; i++){
			String aDelete = (String)deleteList.elementAt(i);
			vars = getVars(aDelete,vars);
		}
		Hashtable renamedVarsTable = makeRenamedVarsTable(vars,uniqueNum);

		// 新しいIfListを作る
		Vector newIfList = new Vector();
		for(int i = 0 ; i < ifList.size() ; i++){
			String newAnIf =
					renameVars((String)ifList.elementAt(i),
							renamedVarsTable);
			newIfList.addElement(newAnIf);
		}
		// 新しいaddListを作る
		Vector newAddList = new Vector();
		for(int i = 0 ; i < addList.size() ; i++){
			String newAnAdd =
					renameVars((String)addList.elementAt(i),
							renamedVarsTable);
			newAddList.addElement(newAnAdd);
		}
		// 新しいdeleteListを作る
		Vector newDeleteList = new Vector();
		for(int i = 0 ; i < deleteList.size() ; i++){
			String newADelete =
					renameVars((String)deleteList.elementAt(i),
							renamedVarsTable);
			newDeleteList.addElement(newADelete);
		}
		// 新しいnameを作る
		String newName = renameVars(name,renamedVarsTable);

		return new Operator(newName,newIfList,newAddList,newDeleteList);
	}

	private Vector getVars(String thePattern,Vector vars){
		StringTokenizer st = new StringTokenizer(thePattern);
		for(int i = 0 ; i < st.countTokens();){
			String tmp = st.nextToken();
			if(var(tmp)){
				vars.addElement(tmp);
			}
		}
		return vars;
	}

	private Hashtable makeRenamedVarsTable(Vector vars,int uniqueNum){
		Hashtable result = new Hashtable();
		for(int i = 0 ; i < vars.size() ; i++){
			String newVar =
					(String)vars.elementAt(i) + uniqueNum;
			result.put((String)vars.elementAt(i),newVar);
		}
		return result;
	}

	private String renameVars(String thePattern,
			Hashtable renamedVarsTable){
		String result = new String();
		StringTokenizer st = new StringTokenizer(thePattern);
		for(int i = 0 ; i < st.countTokens();){
			String tmp = st.nextToken();
			if(var(tmp)){
				result = result + " " +
						(String)renamedVarsTable.get(tmp);
			} else {
				result = result + " " + tmp;
			}
		}
		return result.trim();
	}


	public Operator instantiate(Hashtable theBinding){
		// name を具体化
		String newName =
				instantiateString(name,theBinding);
		// ifList    を具体化
		Vector newIfList = new Vector();
		for(int i = 0 ; i < ifList.size() ; i++){
			String newIf =
					instantiateString((String)ifList.elementAt(i),theBinding);
			newIfList.addElement(newIf);
		}
		// addList   を具体化
		Vector newAddList = new Vector();
		for(int i = 0 ; i < addList.size() ; i++){
			String newAdd =
					instantiateString((String)addList.elementAt(i),theBinding);
			newAddList.addElement(newAdd);
		}
		// deleteListを具体化
		Vector newDeleteList = new Vector();
		for(int i = 0 ; i < deleteList.size() ; i++){
			String newDelete =
					instantiateString((String)deleteList.elementAt(i),theBinding);
			newDeleteList.addElement(newDelete);
		}
		return new Operator(newName,newIfList,newAddList,newDeleteList);
	}

	private String instantiateString(String thePattern, Hashtable theBinding){
		String result = new String();
		StringTokenizer st = new StringTokenizer(thePattern);
		for(int i = 0 ; i < st.countTokens();){
			String tmp = st.nextToken();
			if(var(tmp)){
				String newString = (String)theBinding.get(tmp);
				if(newString == null){
					result = result + " " + tmp;
				} else {
					result = result + " " + newString;
				}
			} else {
				result = result + " " + tmp;
			}
		}
		return result.trim();
	}

	private boolean var(String str1){
		// 先頭が ? なら変数
		return str1.startsWith("?");
	}
}

class Unifier {
	StringTokenizer st1;
	String buffer1[];
	StringTokenizer st2;
	String buffer2[];
	Hashtable vars;

	Unifier(){
		//vars = new Hashtable();
	}

	public boolean unify(String string1,String string2,Hashtable theBindings){
		Hashtable orgBindings = new Hashtable();
		for(Enumeration e = theBindings.keys() ; e.hasMoreElements();){
			String key = (String)e.nextElement();
			String value = (String)theBindings.get(key);
			orgBindings.put(key,value);
		}
		this.vars = theBindings;
		if(unify(string1,string2)){
			return true;
		} else {
			// 失敗したら元に戻す．
			theBindings.clear();
			for(Enumeration e = orgBindings.keys() ; e.hasMoreElements();){
				String key = (String)e.nextElement();
				String value = (String)orgBindings.get(key);
				theBindings.put(key,value);
			}
			return false;
		}
	}

	public boolean unify(String string1,String string2){
		// 同じなら成功
		if(string1.equals(string2)) return true;

		// 各々トークンに分ける
		st1 = new StringTokenizer(string1);
		st2 = new StringTokenizer(string2);

		// 数が異なったら失敗
		if(st1.countTokens() != st2.countTokens()) return false;

		// 定数同士
		int length = st1.countTokens();
		buffer1 = new String[length];
		buffer2 = new String[length];
		for(int i = 0 ; i < length; i++){
			buffer1[i] = st1.nextToken();
			buffer2[i] = st2.nextToken();
		}

		// 初期値としてバインディングが与えられていたら
		if(this.vars.size() != 0){
			for(Enumeration keys = vars.keys(); keys.hasMoreElements();){
				String key = (String)keys.nextElement();
				String value = (String)vars.get(key);
				replaceBuffer(key,value);
			}
		}

		for(int i = 0 ; i < length ; i++){
			if(!tokenMatching(buffer1[i],buffer2[i])){
				return false;
			}
		}

		return true;
	}

	boolean tokenMatching(String token1,String token2){
		if(token1.equals(token2)) return true;
		if( var(token1) && !var(token2)) return varMatching(token1,token2);
		if(!var(token1) &&  var(token2)) return varMatching(token2,token1);
		if( var(token1) &&  var(token2)) return varMatching(token1,token2);
		return false;
	}

	boolean varMatching(String vartoken,String token){
		if(vars.containsKey(vartoken)){
			if(token.equals(vars.get(vartoken))){
				return true;
			} else {
				return false;
			}
		} else {
			replaceBuffer(vartoken,token);
			if(vars.contains(vartoken)){
				replaceBindings(vartoken,token);
			}
			vars.put(vartoken,token);
		}
		return true;
	}

	void replaceBuffer(String preString,String postString){
		for(int i = 0 ; i < buffer1.length ; i++){
			if(preString.equals(buffer1[i])){
				buffer1[i] = postString;
			}
			if(preString.equals(buffer2[i])){
				buffer2[i] = postString;
			}
		}
	}

	void replaceBindings(String preString,String postString){
		Enumeration keys;
		for(keys = vars.keys(); keys.hasMoreElements();){
			String key = (String)keys.nextElement();
			if(preString.equals(vars.get(key))){
				vars.put(key,postString);
			}
		}
	}

	boolean var(String str1){
		// 先頭が ? なら変数
		return str1.startsWith("?");
	}

}
