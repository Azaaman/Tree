package Azamat.Tree.entity;

import javax.persistence.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class MainTreeApplication {
    private static final EntityManagerFactory FACTORY = Persistence.createEntityManagerFactory("main");

    private static final BufferedReader IN = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) throws IOException {
        int x = 0;
        while (x != 4) {
            System.out.println("Выберите дейстие: \n" +
                    "Создание товара [1] \n" +
                    "Перемещение товарар [2] \n" +
                    "Удаление товара [3] \n" +
                    "Завершить выполнение [4]");
            int action = Integer.parseInt(IN.readLine());
            x = action;
            switch (action) {
                case 1:
                    createTree();
                    break;
                case 2:
                    moveTree();
                    break;
                case 3:
                    deleteTree();
                    break;
                case 4:
                    break;
                default:
                    System.out.println("Вы выбрали не существующее действие");
                    break;
            }
        }
    }

    public static void createTree() {
        EntityManager manager = FACTORY.createEntityManager();
        try {
            manager.getTransaction().begin();
            System.out.print("Введите id родительской категорий: ");
            long parentId = Long.parseLong(IN.readLine());
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
                String newName = IN.readLine();
                newTree.setName(newName);
                newTree.setLevel(ParentTree.getRightKey() + 1);
                newTree.setLeftKey(ParentTree.getRightKey());
                newTree.setRightKey(ParentTree.getRightKey()  + 1);
                manager.persist(newTree);
            }
            else{
                Tree newTree = new Tree();
                System.out.print("Введите новое название товара: ");
                String newName = IN.readLine();
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

    public static void deleteTree() {
        EntityManager manager = FACTORY.createEntityManager();
        try {
            manager.getTransaction().begin();
            System.out.print("Введите id: ");
            long deleteTreeId = Long.parseLong(IN.readLine());
            Tree deleteTree = manager.find(Tree.class, deleteTreeId);

            Query queryDelete = manager.createQuery(
                    "delete from Tree t where t.leftKey between ?1 and ?2  "
            );
            queryDelete.setParameter(1, deleteTree.getLeftKey());
            queryDelete.setParameter(2, deleteTree.getRightKey());
            queryDelete.executeUpdate();

            Query query = manager.createQuery(
                    "update Tree t set t.leftKey = t.leftKey -(?1 + 1 - ?2) where t.leftKey > ?3 "
            );
            query.setParameter(1, deleteTree.getRightKey());
            query.setParameter(2, deleteTree.getLeftKey());
            query.setParameter(3, deleteTree.getRightKey());
            query.executeUpdate();

            Query query2 = manager.createQuery(
                    "update Tree t set t.RightKey = t.RightKey -(?1 + 1 - ?2)where t.RightKey >= ?3 "
            );
            query2.setParameter(1, deleteTree.getRightKey());
            query2.setParameter(2, deleteTree.getLeftKey());
            query2.setParameter(3, deleteTree.getRightKey());
            query2.executeUpdate();
            manager.getTransaction().commit();
        } catch (Exception e) {
            manager.getTransaction().rollback();
            e.printStackTrace();
        }
    }


    public static void moveTree(){
        EntityManager manager = FACTORY.createEntityManager();
        try {
            manager.getTransaction().begin();
            System.out.print("Введите id перемещаемой категорий: ");
            long moveTreeId = Long.parseLong(IN.readLine());
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
            long parentTreeId = Long.parseLong(IN.readLine());
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
                List<Tree> trees = treeTypedQuery.getResultList();
                int max = trees.get(0).getRightKey();
                for (Tree tree:trees) {
                    if (tree.getRightKey() > max)
                        max = tree.getRightKey();
                }

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
