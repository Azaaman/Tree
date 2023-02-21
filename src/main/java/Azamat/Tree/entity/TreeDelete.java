package Azamat.Tree.entity;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TreeDelete {
    public static void main(String[] args) throws IOException {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("main");
        EntityManager manager = factory.createEntityManager();
        try {
            manager.getTransaction().begin();
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Введите id: ");
            long deleteTreeId = Long.parseLong(in.readLine());
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
            query.setParameter(1,deleteTree.getRightKey());
            query.setParameter(2,deleteTree.getLeftKey());
            query.setParameter(3,deleteTree.getRightKey());
            query.executeUpdate();

            Query query2 = manager.createQuery(
                    "update Tree t set t.RightKey = t.RightKey -(?1 + 1 - ?2)where t.RightKey >= ?3 "
            );
            query2.setParameter(1,deleteTree.getRightKey());
            query2.setParameter(2,deleteTree.getLeftKey());
            query2.setParameter(3,deleteTree.getRightKey());
            query2.executeUpdate();
            manager.getTransaction().commit();
        }catch (Exception e) {
            manager.getTransaction().rollback();
            e.printStackTrace();
        }
        }
}
