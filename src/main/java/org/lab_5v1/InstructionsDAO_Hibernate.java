package org.lab_5v1;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.lab_5v1.cpu_lib.instructions.Instructions;
import org.lab_5v1.cpu_lib.instructions.InstructionsException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InstructionsDAO_Hibernate extends InstructionsListDAO {
    private List<Instructions> cache = new ArrayList<>();
    private boolean cacheLoaded = false;

    public InstructionsDAO_Hibernate() {
        loadFromDatabase();
    }

    private void loadFromDatabase() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            List<Instructions> dbInstructions = session.createQuery(
                    "FROM Instructions ORDER BY id", Instructions.class).getResultList();

            cache.clear();
            cache.addAll(dbInstructions);
            cacheLoaded = true;

            transaction.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Instructions> getInstructionsList() {
        if (!cacheLoaded) {
            loadFromDatabase();
        }
        return new ArrayList<>(cache);
    }

    @Override
    public void setInstructionsList(List<Instructions> instructionsList) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            session.createQuery("DELETE FROM Instructions").executeUpdate();

            for (Instructions instr : instructionsList) {
                session.persist(instr);
            }

            transaction.commit();

            cache.clear();
            cache.addAll(instructionsList);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addInstr(Instructions instr) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(instr);
            transaction.commit();

            // Обновляем кэш
            cache.add(instr);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public int size() {
        if (!cacheLoaded) {
            loadFromDatabase();
        }
        return cache.size();
    }

    @Override
    public Instructions get(int index) throws InstructionsException {
        if (!cacheLoaded) {
            loadFromDatabase();
        }
        if (index >= 0 && index < cache.size()) {
            return cache.get(index);
        }
        throw new InstructionsException("Index: " + index + ", Size: " + cache.size());
    }

    @Override
    public void set(int index, Instructions instruction) {
        if (index >= 0 && index < cache.size()) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Transaction transaction = session.beginTransaction();

                //обновляем закэшированнные инструкции
                Instructions existing = cache.get(index);
                if (existing != null) {
                    existing.setInstructCode(instruction.getInstructCode());
                    existing.setOperand1(instruction.getOperand1());
                    existing.setOperand2(instruction.getOperand2());

                    session.merge(existing);
                    transaction.commit();
                    cache.set(index, existing);

                }

            } catch (Exception e) {
                try {
                    throw new InstructionsException(e.toString());
                } catch (InstructionsException ex) {
                    throw new RuntimeException(ex);
                }

            }
        } else {
            throw new RuntimeException("Index: " + index);
        }
    }

    @Override
    public void add(Instructions instruction) {
        addInstr(instruction);
    }

    @Override
    public void remove(int index) {
        if (index >= 0 && index < cache.size()) {
            Instructions instr = cache.get(index);
            if (instr != null && instr.getId() != null) {
                try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                    Transaction transaction = session.beginTransaction();

                    // Загружаем инструкцию из БД для удаления
                    Instructions instrToDelete = session.getReference(Instructions.class, instr.getId());
                    if (instrToDelete != null) {
                        session.remove(instrToDelete);
                        transaction.commit();

                        // Обновляем кэш
                        cache.remove(index);
                        System.out.println("[HibernateDAO] Removed instruction with id: " + instr.getId());
                    } else {
                        transaction.rollback();
                        System.out.println("[HibernateDAO] Instruction not found in database");
                    }

                } catch (Exception e) {
                    System.err.println("[HibernateDAO] Error removing instruction: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.err.println("[HibernateDAO] Cannot remove instruction without ID");
            }
        } else {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + cache.size());
        }
    }

    // Также исправьте метод removenstr
    @Override
    public void removenstr(Instructions instr) {
        if (instr != null && instr.getId() != null) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Transaction transaction = session.beginTransaction();

                // Загружаем инструкцию из БД
                Instructions instrToDelete = session.getReference(Instructions.class, instr.getId());
                if (instrToDelete != null) {
                    session.remove(instrToDelete);
                    transaction.commit();

                    // Удаляем из кэша
                    cache.removeIf(i -> i.getId() != null && i.getId().equals(instr.getId()));

                    System.out.println("[HibernateDAO] Removed instruction: " + instr);
                } else {
                    transaction.rollback();
                }

            } catch (Exception e) {
                System.err.println("[HibernateDAO] Error removing instruction: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void clear() throws InstructionsException {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            session.createQuery("DELETE FROM Instructions").executeUpdate();
            transaction.commit();

            // Очищаем кэш
            cache.clear();
            cacheLoaded = false;


        } catch (Exception e) {
            throw new InstructionsException(e.toString());
        }
    }

    @Override
    public Instructions[] toArray() {
        if (!cacheLoaded) {
            loadFromDatabase();
        }
        return cache.toArray(new Instructions[0]);
    }

    @Override
    public List<Instructions> getInternalList() {
        if (!cacheLoaded) {
            loadFromDatabase();
        }
        return new ArrayList<>(cache);
    }

    @Override
    public Iterator<Instructions> iterator() {
        if (!cacheLoaded) {
            loadFromDatabase();
        }
        return cache.iterator();
    }

    @Override
    public boolean isEmpty() {
        if (!cacheLoaded) {
            loadFromDatabase();
        }
        return cache.isEmpty();
    }

    public void syncWithDatabase() {
        loadFromDatabase();
    }
}