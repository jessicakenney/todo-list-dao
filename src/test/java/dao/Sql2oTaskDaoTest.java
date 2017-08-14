package dao;

import com.google.common.annotations.VisibleForTesting;
import models.Task;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class Sql2oTaskDaoTest {

    private Sql2oTaskDao taskDao; //ignore me for now. We'll create this soon.
    private Connection conn; //must be sql2o class conn

    @Before
    public void setUp() throws Exception {
        String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/create.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        taskDao = new Sql2oTaskDao(sql2o); //ignore me for now

        //keep connection open through entire test so it does not get erased.
        conn = sql2o.open();
    }
    @After
    public void tearDown() throws Exception {
        conn.close();
    }

    @Test
    public void addingTasksSetsId() throws Exception   {
        Task task = new Task("mow the lawn");
        int originalTaskId = task.getId();
        taskDao.add(task);
        assertNotEquals(originalTaskId,task.getId());
    }
    @Test
    public void exsistingTasksCanBeFoundById() throws Exception {
        Task task = new Task ("mow the lawn");
        taskDao.add(task);
        Task foundTask = taskDao.findById(task.getId());
        assertEquals(task, foundTask);
    }

    @Test
    public void getAll_allTasksAreFound () throws Exception {
        Task task = new Task ("mow the lawn");
        Task anotherTask = new Task ("clean the dishes");
        taskDao.add(task);
        taskDao.add(anotherTask);
        int number = taskDao.getAll().size();
        assertEquals(2,number );
    }
    @Test
    public void getAll_noTasksAreFound () throws Exception {
        int number = taskDao.getAll().size();
        assertEquals(0,number );
    }

    @Test
    public void update_correctlyUpdates () {
        Task task = new Task("mow the lawn");
        taskDao.add(task);

        taskDao.update(task.getId(),"take a nap");
        Task updatedTask = taskDao.findById(task.getId());
        assertEquals("take a nap",updatedTask.getDescription());
    }

    @Test
    public void deleteById_deletesVeryWell () {
        Task task = new Task ("mow the lawn");
        taskDao.add(task);
        taskDao.deleteById(task.getId());
        assertEquals(0,taskDao.getAll().size());
    }

    @Test
    public void clearAllTasks() {
        Task task = new Task("mow the lawn");
        Task anotherTask = new Task("clean the dishes");
        taskDao.add(task);
        taskDao.add(anotherTask);
        taskDao.clearAllTasks();
        assertEquals(0, taskDao.getAll().size());
    }


}