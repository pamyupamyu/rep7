package planning;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

// http://www.javadrive.jp/tutorial/jlabel/index6.html


class Sample extends JFrame implements ActionListener{

	String sample[] = new String[10];
	ArrayList<String> state = new ArrayList<String>();

    String enter1 = "";
    //ボタンを押すとA,B,Cが入る
    String enter2 = "";
    String sample_text="";
    JTextField text = new JTextField("無実装");
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
    JPanel p = new JPanel();

    //ブロックのそれぞれの初期位置
    int ax=48,ay=240;
    int bx=144,by=240;
    int cx=240,cy=240;
    int ux=0,uy=192;
    int count =0;

      Sample(String title){
	    setTitle(title);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	    //初期状態
		state.add("ontable A");
		state.add("ontable B");
		state.add("ontable C");
		state.add("clear A");
		state.add("clear B");
		state.add("clear C");
		state.add("handEmpty");

		//ボタン、ラベルなどの座標決定
	    p.setLayout(null);
	    label1.setBounds(ax,ay,48,48);
	    label2.setBounds(bx,by,48,48);
	    label3.setBounds(cx,cy,48,48);
	    chara_label.setBounds(ux,uy,48,96);
	    back_label.setBounds(0,0,384,384);

	    explain.setForeground(Color.WHITE);
	    //explain.setBackground(Color.WHITE);
	    explain.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 20));
	    //explain.setOpaque(true);
	    explain.setBounds(5,10,320,20);
	    text.setBounds(20,40,320,30);

	    put.addActionListener(this);
	    put.setBounds(308, 190 , 56, 30);
	    pick.addActionListener(this);
	    pick.setBounds(308, 230 , 56, 30);
	    stack.addActionListener(this);
	    stack.setBounds(308, 270 , 56, 30);
	    play.addActionListener(this);
	    play.setBounds(308, 310 , 56, 30);
	    a_button.addActionListener(this);
	    a_button.setBounds(308, 160 , 17, 15);
	    b_button.addActionListener(this);
	    b_button.setBounds(327, 160 , 17, 15);
	    c_button.addActionListener(this);
	    c_button.setBounds(346, 160 , 17, 15);

	    //貼り付け
	    p.add(put);
	    p.add(pick);
	    p.add(stack);
	    p.add(play);
	    p.add(a_button);
	    p.add(b_button);
	    p.add(c_button);
	    p.add(explain);
	    p.add(text);
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
	       	enter1 =text.getText();
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
        		if(count<10){
        		play(sample[count]);
        		sample_text=sample[count];
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
				System.out.println("OK");
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
		  }
	  }

	  public static void main(String args[]){
		    Sample frame = new Sample("GUIサンプル");
	        frame.setLocation(100, 100); //表示位置
	        frame.setSize(384, 412); //表示サイズ
	        frame.setResizable(false); //リサイズの禁止
		    frame.setVisible(true);
		  }
	}