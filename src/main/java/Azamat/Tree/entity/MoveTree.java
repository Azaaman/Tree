package Azamat.Tree.entity;

import javax.persistence.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class MoveTree {
    public static void main(String[] args) {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("main");
        EntityManager manager = factory.createEntityManager();
        try {
            manager.getTransaction().begin();
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Введите id перемещаемой категорий: ");
            long moveTreeId = Long.parseLong(in.readLine());
            Tree moveTree = manager.find(Tree.class, moveTreeId);

            Query queryMove = manager.createQuery(
                    "update Tree t  set t.RightKey = -t.RightKey  where t.leftKey between ?1 and ?2  "
            );
            queryMove.setParameter(1, moveTree.getLeftKey());
            queryMove.setParameter(2, moveTree.getRightKey());
            queryMove.executeUpdate();

            Query queryMove2 = manager.createQuery(
                    "update Tree t  set t.leftKey = -t.leftKey  where t.leftKey between ?1 and ?2  "
            );
            queryMove2.setParameter(1, moveTree.getLeftKey());
            queryMove2.setParameter(2, moveTree.getRightKey());
            queryMove2.executeUpdate();


            Query query3 = manager.createQuery(
                    "update Tree t set t.leftKey = t.leftKey -(?1 + 1 - ?2) where t.leftKey > ?3 "
            );
            query3.setParameter(1,moveTree.getRightKey());
            query3.setParameter(2,moveTree.getLeftKey());
            query3.setParameter(3,moveTree.getRightKey());
            query3.executeUpdate();

            Query query4 = manager.createQuery(
                    "update Tree t set t.RightKey = t.RightKey -(?1 + 1 - ?2)where t.RightKey >= ?3 "
            );
            query4.setParameter(1,moveTree.getRightKey());
            query4.setParameter(2,moveTree.getLeftKey());
            query4.setParameter(3,moveTree.getRightKey());
            query4.executeUpdate();

            System.out.print("Введите id новой родительской категорий: ");
            long parentTreeId = Long.parseLong(in.readLine());
            if(parentTreeId!=0) {
                Tree parentTree = manager.find(Tree.class, parentTreeId);
                Query query5 = manager.createQuery(
                        "update Tree t set t.RightKey = t.RightKey + (?1 + 1 - ?2) where t.RightKey>=?3 "
                );
                query5.setParameter(1, moveTree.getRightKey());
                query5.setParameter(2, moveTree.getLeftKey());
                query5.setParameter(3, parentTree.getRightKey());
                query5.executeUpdate();

                Query query6 = manager.createQuery(
                        "update Tree t set t.leftKey = t.leftKey + (?1 + 1 - ?2) where t.leftKey > ?3 "
                );
                query6.setParameter(1, moveTree.getRightKey());
                query6.setParameter(2, moveTree.getLeftKey());
                query6.setParameter(3, parentTree.getRightKey());
                query6.executeUpdate();

                manager.refresh(parentTree);

                Query query9 = manager.createQuery(
                        "update Tree t set t.level = t.level + (?1 - ?2 + 1) where t.RightKey < 0 "
                );
                query9.setParameter(1, parentTree.getLevel());
                query9.setParameter(2, moveTree.getLevel());
                query9.executeUpdate();


                Query query7 = manager.createQuery(
                        "update Tree t set t.leftKey = 0 - t.leftKey + (?1 - ?2 - 1) where t.leftKey < 0 "
                );
                query7.setParameter(1, parentTree.getRightKey());
                query7.setParameter(2, moveTree.getRightKey());
                query7.executeUpdate();

                Query query8 = manager.createQuery(
                        "update Tree t set t.RightKey= 0 - t.RightKey + (?1 - ?2 - 1) where t.RightKey < 0 "
                );
                query8.setParameter(1, parentTree.getRightKey());
                query8.setParameter(2, moveTree.getRightKey());
                query8.executeUpdate();
            }else{
                TypedQuery<Tree> treeTypedQuery = manager.createQuery(
                        "select t from Tree t where t.RightKey > 0", Tree.class
                );
                List <Tree> trees = treeTypedQuery.getResultList();
                int max = trees.get(0).getRightKey();
                for (Tree tree:trees) {
                    if (tree.getRightKey() > max)
                        max = tree.getRightKey();
                }
                System.out.println(max);

                Query query7 = manager.createQuery(
                        "update Tree t set t.level = t.level - ?1 where t.RightKey < 0 "
                );
                query7.setParameter(1, moveTree.getLevel());
                query7.executeUpdate();

                Query query5 = manager.createQuery(
                        "update Tree t set t.RightKey= 0 - t.RightKey - ?1 + ?2 +1 where t.RightKey < 0 "
                );
                query5.setParameter(1,moveTree.getLeftKey());
                query5.setParameter(2,max);
                query5.executeUpdate();

                Query query6 = manager.createQuery(
                        "update Tree t set t.leftKey =  0 - t.leftKey -  ?1 + ?2 + 1 where t.leftKey < 0 "
                );
                query6.setParameter(1,moveTree.getLeftKey());
                query6.setParameter(2,max);
                query6.executeUpdate();
            }
            manager.getTransaction().commit();
        } catch (Exception e) {
            manager.getTransaction().rollback();
            e.printStackTrace();
        }
    }
}
