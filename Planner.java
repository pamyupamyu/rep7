import java.util.*;

import javax.swing.*;

import java.awt.BorderLayout;

import java.awt.Button;

import java.awt.Color;

import java.awt.Container;

import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;



import javax.swing.ImageIcon;

import javax.swing.JFrame;

import javax.swing.JLabel;

import javax.swing.JPanel;

import javax.swing.JTextField;

//import sample.InitPanel;

//import sample.InitPanel.MyMouseListener;



public class Planner {

	Vector operators;

	Random rand;

	Vector plan;

	Vector finalgoal;



	//追加

	ArrayList<String> planlist = new ArrayList<String>();

	//追加終了





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


	public static void main(String argv[]){

		//GUI frame = new GUI("GUIサンプル");

		InitPanel frame = new InitPanel("初期状態設定");

		frame.setLocation(100, 100); //表示位置

		frame.setSize(384, 412); //表示サイズ

		frame.setResizable(false); //リサイズの禁止

		frame.setVisible(true);




	}

	//void⇒ArrayList<String>に変更

	public ArrayList<String> start(){

		initOperators();

		//変更:ゴール条件をGoalPanelから読み込む
		//Vector goalList     = initGoalList();
		Vector goalList = new Vector();

		for(int i = 0; i < GoalPanel.goal.size(); i++){
			goalList.addElement(GoalPanel.goal.get(i));
		}
		finalgoal = (Vector)goalList.clone();
		
		//変更:初期状態をInitPanelから読み込む
		//Vector initialState = initInitialState();

		Vector initialState = new Vector();

		for(int i = 0; i < GUI.state.size(); i++){
			initialState.addElement(GUI.state.get(i));
		}

		goalList = goalsort(goalList);

		Hashtable theBinding = new Hashtable();

		plan = new Vector();

		planning(goalList,initialState,theBinding);



		System.out.println("***** This is a plan! *****");

		for(int i = 0 ; i < plan.size() ; i++){

			Operator op = (Operator)plan.elementAt(i);	    

			//修正

			//System.out.println((op.instantiate(theBinding)).name);

			//planlistに実際に行ったplanを保存

			planlist.add((op.instantiate(theBinding)).name);

			System.out.println(planlist.get(i));

			//修正終了

		}

		return planlist;

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
		//System.out.println("index = "+index);
		//System.out.println("operators.size="+operators.size());
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
									if(str[1].equals("?x")) s[0]=goal[1];
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
			for(int i=0;i<finalgoal.size();i++){
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
		//goalList.addElement("F on G");
		//goalList.addElement("ontable C");
		//goalList.addElement("clear A");
		goalList.addElement("B on C"); //(下に積む順番にする)		
		goalList.addElement("A on B");
		//goalList.addElement("ontable E");
		//goalList.addElement("A on B");
		//goalList.addElement("D on E");

		goalList.addElement("ontable C"); //ゴールの順番大事(?x on ?yより前)
		//goalList.addElement("clear A"); //(?x on ?yの後ろ)
		goalList.addElement("handEmpty"); //(最後)
		//goalList.addElement("ontable G");
		finalgoal = (Vector)goalList.clone();
		return goalList;
	}

	public static Vector initInitialState(){
		Vector initialState = new Vector();
		//initialState.addElement("clear A");
		initialState.addElement("clear B");
		initialState.addElement("clear C");
		//initialState.addElement("clear G");

		initialState.addElement("ontable A");
		initialState.addElement("ontable B");
		//initialState.addElement("ontable C");
		//initialState.addElement("ontable D");

		//initialState.addElement("B on C");
		initialState.addElement("C on A");
		//initialState.addElement("D on B");
		//initialState.addElement("E on D");
		//initialState.addElement("F on E");
		//initialState.addElement("G on F");
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








class GUI extends JFrame implements ActionListener{

	String sample[] = new String[10];

	//追加

	ArrayList<String> planlist = (new Planner()).start();

	//追加終了

	static ArrayList<String> state = new ArrayList<String>();
	static ArrayList<String> goal = new ArrayList<String>();



	String enter1 = "";

	//ボタンを押すとA,B,Cが入る

	String enter2 = "";

	String sample_text="";

	//JTextField text = new JTextField("無実装");

	Button put = new Button("下す");

	Button pick = new Button("掴む");

	Button stack = new Button("積む");

	Button a_button = new Button("A");

	Button b_button = new Button("B");

	Button c_button = new Button("C");

	Button play = new Button("読込実行");

	ImageIcon back = new ImageIcon("./back.png");

	ImageIcon human = new ImageIcon("./human.png");

	ImageIcon icon1 = new ImageIcon("./block_a.png");

	ImageIcon icon2 = new ImageIcon("./block_b.png");

	ImageIcon icon3 = new ImageIcon("./block_c.png");

	JLabel back_label = new JLabel(back);

	JLabel chara_label = new JLabel(human);

	JLabel label1 = new JLabel(icon1);

	JLabel label2 = new JLabel(icon2);

	JLabel label3 = new JLabel(icon3);

	JLabel explain = new JLabel();

	JLabel mode = new JLabel();

	JPanel p = new JPanel();


	//ブロックのそれぞれの初期位置

	int ax=48,ay=240;

	int bx=144,by=240;

	int cx=240,cy=240;

	int ux=0,uy=192;

	int count =0;



	GUI(String title){
		setTitle(title);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//p.setVisible(false);

		initread();

		setLocation(100, 100); //表示位置

		setSize(384, 412); //表示サイズ

		setResizable(false); //リサイズの禁止

		//初期状態

		//state.add("ontable A");

		//state.add("ontable B");

		//state.add("ontable C");

		//state.add("clear A");

		//state.add("clear B");

		//state.add("clear C");

		//state.add("handEmpty");





		//ボタン、ラベルなどの座標決定

		p.setLayout(null);

		chara_label.setBounds(ux,uy,48,96);

		back_label.setBounds(0,0,384,384);



		explain.setForeground(Color.WHITE);

		//explain.setBackground(Color.WHITE);

		explain.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 20));

		//explain.setOpaque(true);

		explain.setBounds(5,10,320,20);

		//text.setBounds(20,40,320,30);

		mode.setForeground(Color.YELLOW);
		mode.setFont(new Font("ＭＳ ゴシック",Font.BOLD,20));
		mode.setBounds(5,50,320,20);
		mode.setText("プランニング実行");



		put.addActionListener(this);

		put.setBounds(308, 190 , 56, 30);

		pick.addActionListener(this);

		pick.setBounds(308, 230 , 56, 30);

		stack.addActionListener(this);

		stack.setBounds(308, 270 , 56, 30);

		play.addActionListener(this);

		play.setBounds(260, 310 , 110, 60);

		a_button.addActionListener(this);

		a_button.setBounds(308, 160 , 17, 15);

		b_button.addActionListener(this);

		b_button.setBounds(327, 160 , 17, 15);

		c_button.addActionListener(this);

		c_button.setBounds(346, 160 , 17, 15);



		//貼り付け

		//p.add(put);

		//p.add(pick);

		//p.add(stack);

		p.add(play);

		//p.add(a_button);

		//p.add(b_button);

		//p.add(c_button);

		p.add(explain);

		p.add(mode);
		//p.add(text);

		p.add(label1);

		p.add(label2);

		p.add(label3);

		p.add(chara_label);

		p.add(back_label); //下のが背景側に来る描画順に注意



		sample[0]="pick up B from the table";

		sample[1]="Place B on A";

		sample[2]="remove B from on top A";

		sample[3]="Place B on C";

		sample[4]="pick up A from the table";

		sample[5]="Place A on A";

		sample[6]="remove A from on top A";

		sample[7]="Place A on A";

		sample[8]="remove A from on top A";

		sample[9]="Place A on B";



		Container contentPane = getContentPane();

		contentPane.add(p, BorderLayout.CENTER);

	}

	//ボタン操作受付

	public void actionPerformed(ActionEvent ae) {

		//enter1 =text.getText();

		if(ae.getSource() == put){

			put();

		}else if(ae.getSource() == pick){

			pick(enter2);

		}else if(ae.getSource() == stack){

			stack(enter2);

		}else if(ae.getSource() == a_button){

			enter2 = "A";

		}else if(ae.getSource() == b_button){

			enter2 = "B";

		}else if(ae.getSource() == c_button){

			enter2 = "C";

		}else if(ae.getSource() == play){

			if(count< planlist.size()){

				//変更

				play(planlist.get(count));

				sample_text=planlist.get(count);

				explain.setText(sample_text);

				count++;

			}

		}

	}

	//Listの中にString matchがあるときtrueを返す

	boolean match_list(ArrayList state,String match){

		boolean C=false;

		for(int i=0;i<state.size();i++){

			Object temp = state.get(i);

			String str = temp.toString();

			if(str.equals(match)){

				//System.out.println("OK");

				C =true;

			}

		}

		return C;

	}

	//Listの中身の全表示

	void debug(ArrayList state){

		for(int i=0;i<state.size();i++){

			Object str = state.get(i);

			String temp = str.toString();

			System.out.println(temp);

		}

	}

	//clearなブロックに今持っている(holdingしている)ブロックを置く

	//clearなブロックは引数blockとして選択する

	void stack(String block){

		if(block.equals("A")){

			if(match_list(state,"clear A")&&match_list(state,"holding B")){

				ux=ax-48;

				bx=ax;

				by=ay-48;

				state.remove(state.indexOf("clear A"));

				state.remove(state.indexOf("holding B"));

				state.add("B on A");

				state.add("handEmpty");

				label2.setBounds(bx,by,48,48);

				p.add(label2);

				chara_label.setBounds(ux,uy,48,96);

				p.add(chara_label);

				p.add(back_label);

				//debug(state);

			}else if(match_list(state,"clear A")&&match_list(state,"holding C")){

				ux=ax-48;

				cx=ax;

				cy=ay-48;

				state.remove(state.indexOf("clear A"));

				state.remove(state.indexOf("holding C"));

				state.add("C on A");

				state.add("handEmpty");

				label3.setBounds(cx,cy,48,48);

				p.add(label3);

				chara_label.setBounds(ux,uy,48,96);

				p.add(chara_label);

				p.add(back_label);

				//debug(state);

			}

		}else if(block.equals("B")){

			if(match_list(state,"clear B")&&match_list(state,"holding A")){

				ux=bx-48;

				ax=bx;

				ay=by-48;

				state.remove(state.indexOf("clear B"));

				state.remove(state.indexOf("holding A"));

				state.add("A on B");

				state.add("handEmpty");

				label1.setBounds(ax,ay,48,48);

				p.add(label1);

				chara_label.setBounds(ux,uy,48,96);

				p.add(chara_label);

				p.add(back_label);

				//debug(state);

			}else if(match_list(state,"clear B")&&match_list(state,"holding C")){

				ux=bx-48;

				cx=bx;

				cy=by-48;

				state.remove(state.indexOf("clear B"));

				state.remove(state.indexOf("holding C"));

				state.add("C on B");

				state.add("handEmpty");

				label3.setBounds(cx,cy,48,48);

				p.add(label3);

				chara_label.setBounds(ux,uy,48,96);

				p.add(chara_label);

				p.add(back_label);

				//debug(state);

			}

		}else if(block.equals("C")){

			if(match_list(state,"clear C")&&match_list(state,"holding A")){

				ux=cx-48;

				ax=cx;

				ay=cy-48;

				state.remove(state.indexOf("clear C"));

				state.remove(state.indexOf("holding A"));

				state.add("A on C");

				state.add("handEmpty");

				label1.setBounds(ax,ay,48,48);

				p.add(label1);

				chara_label.setBounds(ux,uy,48,96);

				p.add(chara_label);

				p.add(back_label);

				//debug(state);

			}else if(match_list(state,"clear C")&&match_list(state,"holding B")){

				ux=cx-48;

				bx=cx;

				by=cy-48;

				state.remove(state.indexOf("clear C"));

				state.remove(state.indexOf("holding B"));

				state.add("B on C");

				state.add("handEmpty");

				label2.setBounds(bx,by,48,48);

				p.add(label2);

				chara_label.setBounds(ux,uy,48,96);

				p.add(chara_label);

				p.add(back_label);

				//debug(state);

			}

		}



	}

	//ontableにあるclearなブロックを持つ

	//同じく持つブロックは選択

	void pick(String block){

		if(block.equals("A")){

			if(match_list(state,"clear A")&&match_list(state,"handEmpty")){

				ux=ax-48;

				ax=25;

				ay=316;

				state.remove(state.indexOf("handEmpty"));

				state.add("holding A");

				if(match_list(state,"ontable A")){

					state.remove(state.indexOf("ontable A"));

				}else if(match_list(state,"A on B")){

					state.remove(state.indexOf("A on B"));

					state.add("clear B");

				}else if(match_list(state,"A on C")){

					state.remove(state.indexOf("A on C"));

					state.add("clear C");

				}

				label1.setBounds(ax,ay,48,48);

				p.add(label1);

				chara_label.setBounds(ux,uy,48,96);

				p.add(chara_label);

				p.add(back_label);

				//debug(state);

			}

		}else if(block.equals("B")){

			if(match_list(state,"clear B")&&match_list(state,"handEmpty")){

				ux=bx-48;

				bx=25;

				by=316;

				state.remove(state.indexOf("handEmpty"));

				state.add("holding B");

				if(match_list(state,"ontable B")){

					state.remove(state.indexOf("ontable B"));

				}else if(match_list(state,"B on A")){

					state.remove(state.indexOf("B on A"));

					state.add("clear A");

				}else if(match_list(state,"B on C")){

					state.remove(state.indexOf("B on C"));

					state.add("clear C");

				}

				label2.setBounds(bx,by,48,48);

				p.add(label2);

				chara_label.setBounds(ux,uy,48,96);

				p.add(chara_label);

				p.add(back_label);

				//debug(state);

			}

		}else if(block.equals("C")){

			if(match_list(state,"clear C")&&match_list(state,"handEmpty")){

				ux=cx-48;

				cx=25;

				cy=316;

				state.remove(state.indexOf("handEmpty"));

				state.add("holding C");

				if(match_list(state,"ontable C")){

					state.remove(state.indexOf("ontable C"));

				}else if(match_list(state,"C on A")){

					state.remove(state.indexOf("C on A"));

					state.add("clear A");

				}else if(match_list(state,"C on B")){

					state.remove(state.indexOf("C on B"));

					state.add("clear B");

				}

				label3.setBounds(cx,cy,48,48);

				p.add(label3);

				chara_label.setBounds(ux,uy,48,96);

				p.add(chara_label);

				p.add(back_label);

				//debug(state);

			}

		}



	}

	//持っているブロックをtableに置く

	void put(){

		if(match_list(state,"holding A")){

			ax=48;

			ay=240;

			state.remove(state.indexOf("holding A"));

			state.add("ontable A");

			state.add("handEmpty");

			label1.setBounds(ax,ay,48,48);

			p.add(label1);

			chara_label.setBounds(0,192,48,96);

			p.add(chara_label);

			p.add(back_label);

			//debug(state);

		}else if(match_list(state,"holding B")){

			bx=144;

			by=240;

			state.remove(state.indexOf("holding B"));

			state.add("ontable B");

			state.add("handEmpty");



			label2.setBounds(bx,by,48,48);

			p.add(label2);

			chara_label.setBounds(96,192,48,96);

			p.add(chara_label);

			p.add(back_label);

			//debug(state);

		}else if(match_list(state,"holding C")){

			cx=240;

			cy=240;

			state.remove(state.indexOf("holding C"));

			state.add("ontable C");

			state.add("handEmpty");



			label3.setBounds(cx,cy,48,48);

			p.add(label3);

			chara_label.setBounds(192,192,48,96);

			p.add(chara_label);

			p.add(back_label);

			//debug(state);

		}

	}

	void play(String sample){

		if(sample.equals("pick up A from the table")||sample.equals("remove A from on top B")||sample.equals("remove A from on top C")){

			pick("A");

		}else if(sample.equals("pick up B from the table")||sample.equals("remove B from on top A")||sample.equals("remove B from on top C")){

			pick("B");

		}else if(sample.equals("pick up C from the table")||sample.equals("remove C from on top A")||sample.equals("remove C from on top B")){

			pick("C");

		}else if(sample.equals("Place B on A")||sample.equals("Place C on A")){

			stack("A");

		}else if(sample.equals("Place A on B")||sample.equals("Place C on B")){

			stack("B");

		}else if(sample.equals("Place A on C")||sample.equals("Place B on C")){

			stack("C");

		}else if(sample.equals("put A down on the table") || sample.equals("put B down on the table") || sample.equals("put C down on the table")){

			put();

		}



	}



	/* 追加

	 * 初期状態を読み込む

	 */

	void initread(){

		for(int i = 0; i < state.size(); i++){
			//System.out.println("**********"+state.get(i)+"************");

			if(state.get(i).equals("ontable A")){

				ax = 48;

				ay = 240;

				//label1.setBounds(ax,ay,48,48);

			}

			else if(state.get(i).equals("ontable B")){

				bx = 144;

				by = 240;

				//label2.setBounds(bx,by,48,48);

			}

			else if(state.get(i).equals("ontable C")){

				cx = 240;

				cy = 240;

				//label3.setBounds(cx,cy,48,48);

			}

			else if(state.get(i).equals("holding A")){
				ax = 25;
				ay = 316;

				//label1.setBounds(25,316,48,48);

			}

			else if(state.get(i).equals("holding B")){
				bx = 25;
				by = 316;

				//label2.setBounds(25,316,48,48);

			}

			else if(state.get(i).equals("holding C")){
				cx = 25;
				cy = 316;

				//label3.setBounds(25,316,48,48);

			}

			else if(state.get(i).equals("B on A")){

				bx = ax;

				by = ay - 48;

				//label2.setBounds(bx,by,48,48);

			}

			else if(state.get(i).equals("C on A")){

				cx = ax;

				cy = ay - 48;

				//label3.setBounds(cx,cy,48,48);

			}

			else if(state.get(i).equals("A on B")){

				ax = bx;

				ay = by - 48;

				//label1.setBounds(ax,ay,48,48);

			}

			else if(state.get(i).equals("C on B")){

				cx = bx;

				cy = by - 48;

				//label3.setBounds(cx,cy,48,48);

			}

			else if(state.get(i).equals("A on C")){

				ax = cx;

				ay = cy - 48;

				//label1.setBounds(ax,ay,48,48);

			}

			else if(state.get(i).equals("B on C")){

				bx = cx;

				by = cy - 48;

				//label1.setBounds(bx,by,48,48);

			}

		}
		label1.setBounds(ax,ay,48,48);
		label2.setBounds(bx,by,48,48);
		label3.setBounds(cx,cy,48,48);

	}

	/* 追加

	 * ゴール状態を読み込む

	 */



}
//初期状態を設定する画面
class InitPanel extends JFrame implements ActionListener {
	String text = "";
	Button ok = new Button("完了");
	Button put = new Button("下す");
	Button pick = new Button("掴む");
	Button stack = new Button("積む");
	Button a_button = new Button("A");
	Button b_button = new Button("B");
	Button c_button = new Button("C");
	ImageIcon icon1 = new ImageIcon("./block_a.png");
	ImageIcon icon2 = new ImageIcon("./block_b.png");
	ImageIcon icon3 = new ImageIcon("./block_c.png");
	ImageIcon back = new ImageIcon("./back.png");
	JLabel label1 = new JLabel(icon1);
	JLabel label2 = new JLabel(icon2);
	JLabel label3 = new JLabel(icon3);
	JLabel back_label = new JLabel(back);
	JPanel p2 = new JPanel();
	JLabel mode = new JLabel();

	GUI frame;
	GoalPanel gframe;
	//ArrayList<String> state = new ArrayList<String>();
	String enter2 = "";

	//ブロックのそれぞれの初期位置
	int ax=48,ay=240;
	int bx=144,by=240;
	int cx=240,cy=240;
	int count =0;

	public InitPanel(String title){
		setTitle(title);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


		//初期状態
		frame.state.add("ontable A");	
		frame.state.add("ontable B");
		frame.state.add("ontable C");
		frame.state.add("clear A");
		frame.state.add("clear B");
		frame.state.add("clear C");
		frame.state.add("handEmpty");

		p2.setLayout(null);
		label1.setBounds(ax,ay,48,48);
		label2.setBounds(bx,by,48,48);
		label3.setBounds(cx,cy,48,48);
		back_label.setBounds(0,0,384,384);

		mode.setForeground(Color.YELLOW);
		mode.setFont(new Font("ＭＳ ゴシック",Font.BOLD,20));
		mode.setBounds(5,50,320,20);
		mode.setText("初期状態を設定してください");

		// リスナーを登録
		MyMouseListener listener1 = new MyMouseListener(label1);
		MyMouseListener listener2 = new MyMouseListener(label2);
		MyMouseListener listener3 = new MyMouseListener(label3);

		label1.addMouseListener(listener1);
		label1.addMouseMotionListener(listener1);
		label2.addMouseListener(listener2);
		label2.addMouseMotionListener(listener2);
		label3.addMouseListener(listener3);
		label3.addMouseMotionListener(listener3);

		ok.addActionListener(this);
		ok.setBounds(240,310,100,50);
		put.addActionListener(this);
		put.setBounds(308, 190 , 56, 30);
		pick.addActionListener(this);
		pick.setBounds(308, 230 , 56, 30);
		stack.addActionListener(this);
		stack.setBounds(308, 270 , 56, 30);
		a_button.addActionListener(this);
		a_button.setBounds(308, 160 , 17, 15);
		b_button.addActionListener(this);
		b_button.setBounds(327, 160 , 17, 15);
		c_button.addActionListener(this);
		c_button.setBounds(346, 160 , 17, 15);

		//設置
		p2.add(ok);
		p2.add(put);
		p2.add(pick);
		p2.add(stack);
		p2.add(a_button);
		p2.add(b_button);
		p2.add(c_button);
		p2.add(mode);
		p2.add(label1);
		p2.add(label2);
		p2.add(label3);
		p2.add(back_label);

		Container contentPane = getContentPane();
		contentPane.add(p2, BorderLayout.CENTER);
	}



	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

		if(e.getSource() == put){
			put();
		}else if(e.getSource() == pick){
			pick(enter2);
		}else if(e.getSource() == stack){
			stack(enter2);
		}else if(e.getSource() == a_button){
			enter2 = "A";
		}else if(e.getSource() == b_button){
			enter2 = "B";
		}else if(e.getSource() == c_button){
			enter2 = "C";
		}else if(e.getSource() == ok){
			//System.exit(0);
			gframe = new GoalPanel("ゴール状態変更");
			gframe.setVisible(true);
			setVisible(false);
			//frame.p.setVisible(true);

		}
	}

	boolean match_list(ArrayList state,String match){

		boolean C=false;

		for(int i=0;i<state.size();i++){

			Object temp = state.get(i);

			String str = temp.toString();

			if(str.equals(match)){

				//System.out.println("OK");

				C =true;

			}

		}
		return C;

	}
	//clearなブロックに今持っている(holdingしている)ブロックを置く

	//clearなブロックは引数blockとして選択する

	void stack(String block){

		if(block.equals("A")){

			if(match_list(frame.state,"clear A")&&match_list(frame.state,"holding B")){

				bx=ax;

				by=ay-48;

				frame.state.remove(frame.state.indexOf("clear A"));

				frame.state.remove(frame.state.indexOf("holding B"));

				frame.state.add("B on A");

				frame.state.add("handEmpty");

				label2.setBounds(bx,by,48,48);

				p2.add(label2);

				p2.add(back_label);

				//debug(state);

			}else if(match_list(frame.state,"clear A")&&match_list(frame.state,"holding C")){

				cx=ax;

				cy=ay-48;

				frame.state.remove(frame.state.indexOf("clear A"));

				frame.state.remove(frame.state.indexOf("holding C"));

				frame.state.add("C on A");

				frame.state.add("handEmpty");

				label3.setBounds(cx,cy,48,48);

				p2.add(label3);

				p2.add(back_label);

				//debug(state);

			}

		}else if(block.equals("B")){

			if(match_list(frame.state,"clear B")&&match_list(frame.state,"holding A")){

				ax=bx;

				ay=by-48;

				frame.state.remove(frame.state.indexOf("clear B"));

				frame.state.remove(frame.state.indexOf("holding A"));

				frame.state.add("A on B");

				frame.state.add("handEmpty");

				label1.setBounds(ax,ay,48,48);

				p2.add(label1);


				p2.add(back_label);

				//debug(state);

			}else if(match_list(frame.state,"clear B")&&match_list(frame.state,"holding C")){


				cx=bx;

				cy=by-48;

				frame.state.remove(frame.state.indexOf("clear B"));

				frame.state.remove(frame.state.indexOf("holding C"));

				frame.state.add("C on B");

				frame.state.add("handEmpty");

				label3.setBounds(cx,cy,48,48);

				p2.add(label3);

				p2.add(back_label);

				//debug(state);

			}

		}else if(block.equals("C")){

			if(match_list(frame.state,"clear C")&&match_list(frame.state,"holding A")){


				ax=cx;

				ay=cy-48;

				frame.state.remove(frame.state.indexOf("clear C"));

				frame.state.remove(frame.state.indexOf("holding A"));

				frame.state.add("A on C");

				frame.state.add("handEmpty");

				label1.setBounds(ax,ay,48,48);

				p2.add(label1);

				p2.add(back_label);

				//debug(state);

			}else if(match_list(frame.state,"clear C")&&match_list(frame.state,"holding B")){

				bx=cx;

				by=cy-48;

				frame.state.remove(frame.state.indexOf("clear C"));

				frame.state.remove(frame.state.indexOf("holding B"));

				frame.state.add("B on C");

				frame.state.add("handEmpty");

				label2.setBounds(bx,by,48,48);

				p2.add(label2);

				p2.add(back_label);

				//debug(state);

			}

		}



	}

	//ontableにあるclearなブロックを持つ

	//同じく持つブロックは選択

	void pick(String block){

		if(block.equals("A")){

			if(match_list(frame.state,"clear A")&&match_list(frame.state,"handEmpty")){

				ax=25;

				ay=316;

				frame.state.remove(frame.state.indexOf("handEmpty"));

				frame.state.add("holding A");

				if(match_list(frame.state,"ontable A")){

					frame.state.remove(frame.state.indexOf("ontable A"));

				}else if(match_list(frame.state,"A on B")){

					frame.state.remove(frame.state.indexOf("A on B"));

					frame.state.add("clear B");

				}else if(match_list(frame.state,"A on C")){

					frame.state.remove(frame.state.indexOf("A on C"));

					frame.state.add("clear C");

				}

				label1.setBounds(ax,ay,48,48);

				p2.add(label1);

				p2.add(back_label);

				//debug(state);

			}

		}else if(block.equals("B")){

			if(match_list(frame.state,"clear B")&&match_list(frame.state,"handEmpty")){


				bx=25;

				by=316;

				frame.state.remove(frame.state.indexOf("handEmpty"));

				frame.state.add("holding B");

				if(match_list(frame.state,"ontable B")){

					frame.state.remove(frame.state.indexOf("ontable B"));

				}else if(match_list(frame.state,"B on A")){

					frame.state.remove(frame.state.indexOf("B on A"));

					frame.state.add("clear A");

				}else if(match_list(frame.state,"B on C")){

					frame.state.remove(frame.state.indexOf("B on C"));

					frame.state.add("clear C");

				}

				label2.setBounds(bx,by,48,48);

				p2.add(label2);


				p2.add(back_label);

				//debug(state);

			}

		}else if(block.equals("C")){

			if(match_list(frame.state,"clear C")&&match_list(frame.state,"handEmpty")){


				cx=25;

				cy=316;

				frame.state.remove(frame.state.indexOf("handEmpty"));

				frame.state.add("holding C");

				if(match_list(frame.state,"ontable C")){

					frame.state.remove(frame.state.indexOf("ontable C"));

				}else if(match_list(frame.state,"C on A")){

					frame.state.remove(frame.state.indexOf("C on A"));

					frame.state.add("clear A");

				}else if(match_list(frame.state,"C on B")){

					frame.state.remove(frame.state.indexOf("C on B"));

					frame.state.add("clear B");

				}

				label3.setBounds(cx,cy,48,48);

				p2.add(label3);

				p2.add(back_label);

				//debug(state);

			}

		}



	}

	//持っているブロックをtableに置く

	void put(){

		if(match_list(frame.state,"holding A")){

			ax=48;

			ay=240;

			frame.state.remove(frame.state.indexOf("holding A"));

			frame.state.add("ontable A");

			frame.state.add("handEmpty");



			label1.setBounds(ax,ay,48,48);

			p2.add(label1);

			p2.add(back_label);

			//debug(state);

		}else if(match_list(frame.state,"holding B")){

			bx=144;

			by=240;

			frame.state.remove(frame.state.indexOf("holding B"));

			frame.state.add("ontable B");

			frame.state.add("handEmpty");

			label2.setBounds(bx,by,48,48);

			p2.add(label2);

			p2.add(back_label);

			//debug(state);

		}else if(match_list(frame.state,"holding C")){

			cx=240;

			cy=240;

			frame.state.remove(frame.state.indexOf("holding C"));

			frame.state.add("ontable C");

			frame.state.add("handEmpty");



			label3.setBounds(cx,cy,48,48);

			p2.add(label3);


			p2.add(back_label);

			//debug(state);

		}

	}


	class MyMouseListener extends MouseAdapter{
		int dx;
		int dy;
		int sx1,sx2,sx3;
		int sy1,sy2,sy3;

		JLabel label;

		MyMouseListener(JLabel a){
			label = a;
		}

		public void mouseDragged(MouseEvent e) {
			// マウスの座標から画像(ラベル)の左上の座標を取得する
			int x = e.getXOnScreen() - dx;
			int y = e.getYOnScreen() - dy;
			//デバッグ用
			System.out.println("x="+x);
			System.out.println("y="+y);

			//当たり判定		      
			//画面外や地面の外に出ないための処理※holdingに行かない
			if(x < 0)
				x = 0;
			if(x > 328)
				x = 328;
			if(y < 0)
				y = 0;
			if(y > 240)
				y = 240;

			//持っているのがAであるとき
			if(label.getX() == ax && label.getY() == ay){
				if(x > label2.getX() + 48)
					x = label2.getX() + 48;
			}

			//持っているのがBであるとき
			if(label.getX() == bx && label.getY() == by){
				sx1 = Math.abs(label.getX() - label1.getX());
				sy1 = Math.abs(label.getY() - label1.getY());
				sx3 = Math.abs(label.getX() - label3.getX());
				sy3 = Math.abs(label.getY() - label3.getY());
				if(sx1 < 48 && sy1 <48){
					x = ax;
					y = ay-48;
				}
				if(sx3 < 48 && sy3 < 48){
					x = cx;
					y = cy-48;
				}
				bx = x;
				by = y;
			}

			//持っているのがCであるとき
			if(label.getX() == cx && label.getY() == cy){
				sx1 = Math.abs(label.getX() - label1.getX());
				sy1 = Math.abs(label.getY() - label1.getY());
				sx2 = Math.abs(label.getX() - label2.getX());
				sy2 = Math.abs(label.getY() - label2.getY());
				if(sx1 < 48 && sy1 <48){
					x = ax;
					y = ay-48;
				}
				if(sx2 < 48 && sy2 < 48){
					x = bx;
					y = by-48;
				}
				cx = x;
				cy = y;
			}

			label.setLocation(x, y);
		}

		public void mousePressed(MouseEvent e) {
			// 画面上でマウスで押した絶対座標からラベルの左上の座標の差を取って相対座標にする
			dx = e.getXOnScreen() - label.getX();
			dy= e.getYOnScreen() - label.getY();
		}
	}
}

//Goal状態を変更する画面
class GoalPanel extends JFrame implements ActionListener{
	String text = "";
	Button ok = new Button("完了");
	Button put = new Button("下す");
	Button pick = new Button("掴む");
	Button stack = new Button("積む");
	Button a_button = new Button("A");
	Button b_button = new Button("B");
	Button c_button = new Button("C");
	ImageIcon icon1 = new ImageIcon("./block_a.png");
	ImageIcon icon2 = new ImageIcon("./block_b.png");
	ImageIcon icon3 = new ImageIcon("./block_c.png");
	ImageIcon back = new ImageIcon("./back.png");
	JLabel label1 = new JLabel(icon1);
	JLabel label2 = new JLabel(icon2);
	JLabel label3 = new JLabel(icon3);
	JLabel back_label = new JLabel(back);
	JPanel p3 = new JPanel();
	JLabel mode = new JLabel();
	static ArrayList<String> goal = new ArrayList<String>();

	GUI frame;
	//ArrayList<String> state = new ArrayList<String>();
	String enter2 = "";

	//ブロックのそれぞれの初期位置
	int ax=48,ay=240;
	int bx=144,by=240;
	int cx=240,cy=240;
	int count =0;

	public GoalPanel(String title){
		setTitle(title);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


		setLocation(100, 100); //表示位置

		setSize(384, 412); //表示サイズ

		setResizable(false); //リサイズの禁止

		//初期状態

		goal.add("ontable C");
		goal.add("B on C");
		goal.add("A on B");
		goal.add("clear A");
		goal.add("handEmpty");


		goalread();

		p3.setLayout(null);
		label1.setBounds(ax,ay,48,48);
		label2.setBounds(bx,by,48,48);
		label3.setBounds(cx,cy,48,48);
		back_label.setBounds(0,0,384,384);

		mode.setForeground(Color.YELLOW);
		mode.setFont(new Font("ＭＳ ゴシック",Font.BOLD,20));
		mode.setBounds(5,50,320,20);
		mode.setText("ゴール目標を設定してください");




		// リスナーを登録
		MyMouseListener listener1 = new MyMouseListener(label1);
		MyMouseListener listener2 = new MyMouseListener(label2);
		MyMouseListener listener3 = new MyMouseListener(label3);

		label1.addMouseListener(listener1);
		label1.addMouseMotionListener(listener1);
		label2.addMouseListener(listener2);
		label2.addMouseMotionListener(listener2);
		label3.addMouseListener(listener3);
		label3.addMouseMotionListener(listener3);

		ok.addActionListener(this);
		ok.setBounds(240,310,100,50);
		put.addActionListener(this);
		put.setBounds(308, 190 , 56, 30);
		pick.addActionListener(this);
		pick.setBounds(308, 230 , 56, 30);
		stack.addActionListener(this);
		stack.setBounds(308, 270 , 56, 30);
		a_button.addActionListener(this);
		a_button.setBounds(308, 160 , 17, 15);
		b_button.addActionListener(this);
		b_button.setBounds(327, 160 , 17, 15);
		c_button.addActionListener(this);
		c_button.setBounds(346, 160 , 17, 15);

		//設置
		p3.add(ok);
		p3.add(put);
		p3.add(pick);
		p3.add(stack);
		p3.add(a_button);
		p3.add(b_button);
		p3.add(c_button);
		p3.add(mode);
		p3.add(label1);
		p3.add(label2);
		p3.add(label3);
		p3.add(back_label);


		Container contentPane = getContentPane();
		contentPane.add(p3, BorderLayout.CENTER);
	}



	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

		if(e.getSource() == put){
			put();
		}else if(e.getSource() == pick){
			pick(enter2);
		}else if(e.getSource() == stack){
			stack(enter2);
		}else if(e.getSource() == a_button){
			enter2 = "A";
		}else if(e.getSource() == b_button){
			enter2 = "B";
		}else if(e.getSource() == c_button){
			enter2 = "C";
		}else if(e.getSource() == ok){
			//System.exit(0);
			frame = new GUI("メイン");
			frame.setVisible(true);
			setVisible(false);
			//frame.p.setVisible(true);

		}
	}

	boolean match_list(ArrayList state,String match){

		boolean C=false;

		for(int i=0;i<state.size();i++){

			Object temp = state.get(i);

			String str = temp.toString();

			if(str.equals(match)){

				//System.out.println("OK");

				C =true;

			}

		}
		return C;

	}
	//clearなブロックに今持っている(holdingしている)ブロックを置く

	//clearなブロックは引数blockとして選択する

	void stack(String block){

		if(block.equals("A")){

			if(match_list(goal,"clear A")&&match_list(goal,"holding B")){

				bx=ax;

				by=ay-48;

				goal.remove(goal.indexOf("clear A"));

				goal.remove(goal.indexOf("holding B"));

				goal.add("B on A");

				goal.add("handEmpty");

				label2.setBounds(bx,by,48,48);

				p3.add(label2);

				p3.add(back_label);

				//debug(state);

			}else if(match_list(goal,"clear A")&&match_list(goal,"holding C")){

				cx=ax;

				cy=ay-48;

				goal.remove(goal.indexOf("clear A"));

				goal.remove(goal.indexOf("holding C"));

				goal.add("C on A");

				goal.add("handEmpty");

				label3.setBounds(cx,cy,48,48);

				p3.add(label3);

				p3.add(back_label);

				//debug(state);

			}

		}else if(block.equals("B")){

			if(match_list(goal,"clear B")&&match_list(goal,"holding A")){

				ax=bx;

				ay=by-48;

				goal.remove(goal.indexOf("clear B"));

				goal.remove(goal.indexOf("holding A"));

				goal.add("A on B");

				goal.add("handEmpty");

				label1.setBounds(ax,ay,48,48);

				p3.add(label1);


				p3.add(back_label);

				//debug(state);

			}else if(match_list(goal,"clear B")&&match_list(goal,"holding C")){


				cx=bx;

				cy=by-48;

				goal.remove(goal.indexOf("clear B"));

				goal.remove(goal.indexOf("holding C"));

				goal.add("C on B");

				goal.add("handEmpty");

				label3.setBounds(cx,cy,48,48);

				p3.add(label3);

				p3.add(back_label);

				//debug(state);

			}

		}else if(block.equals("C")){

			if(match_list(goal,"clear C")&&match_list(goal,"holding A")){


				ax=cx;

				ay=cy-48;

				goal.remove(goal.indexOf("clear C"));

				goal.remove(goal.indexOf("holding A"));

				goal.add("A on C");

				goal.add("handEmpty");

				label1.setBounds(ax,ay,48,48);

				p3.add(label1);

				p3.add(back_label);

				//debug(state);

			}else if(match_list(goal,"clear C")&&match_list(goal,"holding B")){

				bx=cx;

				by=cy-48;

				goal.remove(goal.indexOf("clear C"));

				goal.remove(goal.indexOf("holding B"));

				goal.add("B on C");

				goal.add("handEmpty");

				label2.setBounds(bx,by,48,48);

				p3.add(label2);

				p3.add(back_label);

				//debug(state);

			}

		}



	}

	//ontableにあるclearなブロックを持つ

	//同じく持つブロックは選択

	void pick(String block){

		if(block.equals("A")){

			if(match_list(goal,"clear A")&&match_list(goal,"handEmpty")){

				ax=25;

				ay=316;

				goal.remove(goal.indexOf("handEmpty"));

				goal.add("holding A");

				if(match_list(goal,"ontable A")){

					goal.remove(goal.indexOf("ontable A"));

				}else if(match_list(goal,"A on B")){

					goal.remove(goal.indexOf("A on B"));

					goal.add("clear B");

				}else if(match_list(goal,"A on C")){

					goal.remove(goal.indexOf("A on C"));

					goal.add("clear C");

				}

				label1.setBounds(ax,ay,48,48);

				p3.add(label1);

				p3.add(back_label);
				//debug(state);

			}

		}else if(block.equals("B")){

			if(match_list(goal,"clear B")&&match_list(goal,"handEmpty")){


				bx=25;

				by=316;

				goal.remove(goal.indexOf("handEmpty"));

				goal.add("holding B");

				if(match_list(goal,"ontable B")){

					goal.remove(goal.indexOf("ontable B"));

				}else if(match_list(goal,"B on A")){

					goal.remove(goal.indexOf("B on A"));

					goal.add("clear A");

				}else if(match_list(goal,"B on C")){

					goal.remove(goal.indexOf("B on C"));

					goal.add("clear C");

				}

				label2.setBounds(bx,by,48,48);

				p3.add(label2);


				p3.add(back_label);

				//debug(state);

			}

		}else if(block.equals("C")){

			if(match_list(goal,"clear C")&&match_list(goal,"handEmpty")){


				cx=25;

				cy=316;

				goal.remove(goal.indexOf("handEmpty"));

				goal.add("holding C");

				if(match_list(goal,"ontable C")){

					goal.remove(goal.indexOf("ontable C"));

				}else if(match_list(goal,"C on A")){

					goal.remove(goal.indexOf("C on A"));

					goal.add("clear A");

				}else if(match_list(goal,"C on B")){

					goal.remove(goal.indexOf("C on B"));

					goal.add("clear B");

				}

				label3.setBounds(cx,cy,48,48);

				p3.add(label3);

				p3.add(back_label);

				//debug(state);

			}

		}



	}

	//持っているブロックをtableに置く

	void put(){

		if(match_list(goal,"holding A")){

			ax=48;

			ay=240;

			goal.remove(goal.indexOf("holding A"));

			goal.add("ontable A");

			goal.add("handEmpty");



			label1.setBounds(ax,ay,48,48);

			p3.add(label1);

			p3.add(back_label);

			//debug(state);

		}else if(match_list(goal,"holding B")){

			bx=144;

			by=240;

			goal.remove(goal.indexOf("holding B"));

			goal.add("ontable B");

			goal.add("handEmpty");

			label2.setBounds(bx,by,48,48);

			p3.add(label2);

			p3.add(back_label);

			//debug(state);

		}else if(match_list(goal,"holding C")){

			cx=240;

			cy=240;

			goal.remove(goal.indexOf("holding C"));
			goal.add("ontable C");
			goal.add("handEmpty");



			label3.setBounds(cx,cy,48,48);

			p3.add(label3);


			p3.add(back_label);

			//debug(state);

		}

	}


	class MyMouseListener extends MouseAdapter{
		int dx;
		int dy;
		int sx1,sx2,sx3;
		int sy1,sy2,sy3;

		JLabel label;

		MyMouseListener(JLabel a){
			label = a;
		}

		public void mouseDragged(MouseEvent e) {
			// マウスの座標から画像(ラベル)の左上の座標を取得する
			int x = e.getXOnScreen() - dx;
			int y = e.getYOnScreen() - dy;
			//デバッグ用
			System.out.println("x="+x);
			System.out.println("y="+y);

			//当たり判定		      
			//画面外や地面の外に出ないための処理※holdingに行かない
			if(x < 0)
				x = 0;
			if(x > 328)
				x = 328;
			if(y < 0)
				y = 0;
			if(y > 240)
				y = 240;

			//持っているのがAであるとき
			if(label.getX() == ax && label.getY() == ay){
				if(x > label2.getX() + 48)
					x = label2.getX() + 48;
			}

			//持っているのがBであるとき
			if(label.getX() == bx && label.getY() == by){
				sx1 = Math.abs(label.getX() - label1.getX());
				sy1 = Math.abs(label.getY() - label1.getY());
				sx3 = Math.abs(label.getX() - label3.getX());
				sy3 = Math.abs(label.getY() - label3.getY());
				if(sx1 < 48 && sy1 <48){
					x = ax;
					y = ay-48;
				}
				if(sx3 < 48 && sy3 < 48){
					x = cx;
					y = cy-48;
				}
				bx = x;
				by = y;
			}

			//持っているのがCであるとき
			if(label.getX() == cx && label.getY() == cy){
				sx1 = Math.abs(label.getX() - label1.getX());
				sy1 = Math.abs(label.getY() - label1.getY());
				sx2 = Math.abs(label.getX() - label2.getX());
				sy2 = Math.abs(label.getY() - label2.getY());
				if(sx1 < 48 && sy1 <48){
					x = ax;
					y = ay-48;
				}
				if(sx2 < 48 && sy2 < 48){
					x = bx;
					y = by-48;
				}
				cx = x;
				cy = y;
			}

			label.setLocation(x, y);
		}

		public void mousePressed(MouseEvent e) {
			// 画面上でマウスで押した絶対座標からラベルの左上の座標の差を取って相対座標にする
			dx = e.getXOnScreen() - label.getX();
			dy= e.getYOnScreen() - label.getY();
		}
	}

	void goalread(){

		for(int i = 0; i < goal.size(); i++){
			//System.out.println("**********"+state.get(i)+"************");

			if(goal.get(i).equals("ontable A")){

				ax = 48;

				ay = 240;


			}

			else if(goal.get(i).equals("ontable B")){

				bx = 144;

				by = 240;


			}

			else if(goal.get(i).equals("ontable C")){

				cx = 240;

				cy = 240;


			}

			else if(goal.get(i).equals("holding A")){
				ax = 25;
				ay = 316;


			}

			else if(goal.get(i).equals("holding B")){
				bx = 25;
				by = 316;


			}

			else if(goal.get(i).equals("holding C")){
				cx = 25;
				cy = 316;


			}

			else if(goal.get(i).equals("B on A")){

				bx = ax;

				by = ay - 48;


			}

			else if(goal.get(i).equals("C on A")){

				cx = ax;

				cy = ay - 48;


			}

			else if(goal.get(i).equals("A on B")){

				ax = bx;

				ay = by - 48;


			}

			else if(goal.get(i).equals("C on B")){

				cx = bx;

				cy = by - 48;


			}

			else if(goal.get(i).equals("A on C")){

				ax = cx;

				ay = cy - 48;


			}

			else if(goal.get(i).equals("B on C")){

				bx = cx;

				by = cy - 48;


			}

		}
		label1.setBounds(ax,ay,48,48);
		label2.setBounds(bx,by,48,48);
		label3.setBounds(cx,cy,48,48);

	}
}
