package Azamat.Tree.entity;

import javax.persistence.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TreeApplication {
    public static void main(String[] args) throws IOException {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("main");
        EntityManager manager = factory.createEntityManager();
        try {
            manager.getTransaction().begin();
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Введите id родительской категорий: ");
            long parentId = Long.parseLong(in.readLine());
            Tree ParentTree = manager.find(Tree.class, parentId);
            if(parentId != 0){
            Query query = manager.createQuery(
                    "update Tree t set t.leftKey = t.leftKey + 2 where t.leftKey > ?1 "
            );query.setParameter(1,ParentTree.getRightKey());
            query.executeUpdate();

            Query query2 = manager.createQuery(
                    "update Tree t set t.RightKey = t.RightKey + 2 where t.RightKey >= ?1 "
            );
            query2.setParameter(1,ParentTree.getRightKey());
            query2.executeUpdate();

            Tree newTree = new Tree();
            System.out.print("Введите новое название товара: ");
            String newName = in.readLine();
            newTree.setName(newName);
            newTree.setLevel(ParentTree.getRightKey() + 1);
            newTree.setLeftKey(ParentTree.getRightKey());
            newTree.setRightKey(ParentTree.getRightKey()  + 1);
            manager.persist(newTree);
            }
            else{
                Tree newTree = new Tree();
                System.out.print("Введите новое название товара: ");
                String newName = in.readLine();
                newTree.setName(newName);
                TypedQuery<Tree> treeTypedQuery = manager.createQuery(
                        "select t from Tree t", Tree.class
                );
                List <Tree> trees = treeTypedQuery.getResultList();
                    int max = trees.get(0).getRightKey();
                    for (Tree tree:trees) {
                        if (tree.getRightKey() > max)
                            max = tree.getRightKey();
                    }
                newTree.setLeftKey(max+1);
                newTree.setRightKey(max+2);
                newTree.setLevel(0);
                manager.persist(newTree);
            }
            manager.getTransaction().commit();
        } catch (Exception e) {
            manager.getTransaction().rollback();
            e.printStackTrace();
        }
    }
}
