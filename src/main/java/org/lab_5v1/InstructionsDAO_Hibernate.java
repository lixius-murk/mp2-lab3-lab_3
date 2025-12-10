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
    public void removenstr(Instructions instr) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.remove(instr);
            transaction.commit();

            // Обновляем кэш
            cache.remove(instr);


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

                // Получаем существующую инструкцию
                Instructions existing = cache.get(index);
                if (existing != null) {
                    // Копируем данные из новой инструкции
                    existing.setType(instruction.getInstructCode());
                    existing.setOperands(instruction.getOperands());

                    session.merge(existing);
                    transaction.commit();

                    // Обновляем кэш
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
            if (instr != null) {
                removenstr(instr);
            }
        } else {
            throw new RuntimeException("Index: " + index);
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