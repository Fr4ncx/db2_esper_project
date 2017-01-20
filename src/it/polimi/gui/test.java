package it.polimi.gui;



import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
 
public class test extends JFrame
{
    private JTree tree;
    public test()
    {
        //create the root node
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Consumption values");
        DefaultMutableTreeNode vegetableNode = new DefaultMutableTreeNode("Device id : 12");
        vegetableNode.add(new DefaultMutableTreeNode("Capsicum"));
        vegetableNode.add(new DefaultMutableTreeNode("Carrot"));
        vegetableNode.add(new DefaultMutableTreeNode("Tomato"));
        vegetableNode.add(new DefaultMutableTreeNode("Potato"));
        
        DefaultMutableTreeNode fruitNode = new DefaultMutableTreeNode("Device id : 13");
        fruitNode.add(new DefaultMutableTreeNode("Banana"));
        fruitNode.add(new DefaultMutableTreeNode("Mango"));
        fruitNode.add(new DefaultMutableTreeNode("Apple"));
        fruitNode.add(new DefaultMutableTreeNode("Grapes"));
        fruitNode.add(new DefaultMutableTreeNode("Orange"));
 
        //add the child nodes to the root node
        root.add(fruitNode);
        root.add(vegetableNode);
         
        //create the tree by passing in the root node
        tree = new JTree(root);
        add(tree);
        
      
         
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("JTree Example");       
        this.pack();
        this.setVisible(true);
    }
     
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new test();
            }
        });
    }       
}