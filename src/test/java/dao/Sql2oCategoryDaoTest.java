package dao;

import models.Category;
import models.Task;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import static junit.framework.TestCase.assertEquals;
//import static org.junit.Assert.*;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Guest on 8/14/17.
 */
public class Sql2oCategoryDaoTest {

    private Sql2oCategoryDao categoryDao; //ignore me for now. We'll create this soon.
    private Sql2oTaskDao taskDao;
    private Connection conn; //must be sql2o class conn

    @Before
    public void setUp() throws Exception {
        String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/create.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        categoryDao = new Sql2oCategoryDao(sql2o);
        taskDao = new Sql2oTaskDao(sql2o);
        //keep connection open through entire test so it does not get erased.
        conn = sql2o.open();
    }
    @After
    public void tearDown() throws Exception {
        conn.close();
    }

    @Test
    public void addingCategorySetsId() throws Exception   {
        Category category = new Category("home");
        int originalCategoryId = category.getId();
        categoryDao.add(category);
        assertNotEquals(originalCategoryId,category.getId());
    }

    @Test
    public void exsistingTasksCanBeFoundById() throws Exception {
        Category category = new Category("home");
        categoryDao.add(category);
        Category foundCategory = categoryDao.findById(category.getId());
        assertEquals(category, foundCategory);
    }

    @Test
    public void getAll_allCategoriesAreFound () throws Exception {
        Category category = new Category("home");
        Category anotherCategory = new Category("work");
        categoryDao.add(category);
        categoryDao.add(anotherCategory);
        int number = categoryDao.getAll().size();
        assertEquals(2,number );
    }
    @Test
    public void getAll_noCategoriesAreFound () throws Exception {
        int number = categoryDao.getAll().size();
        assertEquals(0,number );
    }

    @Test
    public void update_correctlyUpdates () {
        Category category = new Category("home");
        categoryDao.add(category);
        categoryDao.update(category.getId(),"Home");
        Category updatedCategory = categoryDao.findById(category.getId());
        assertEquals("Home",updatedCategory.getName());
    }

    @Test
    public void deleteById_deletesVeryWell () {
        Category category = new Category("home");
        categoryDao.add(category);
        categoryDao.deleteById(category.getId());
        assertEquals(0,categoryDao.getAll().size());
    }

    @Test
    public void clearAllTasks() {
        Category category = new Category("home");
        Category anotherCategory = new Category("work");
        categoryDao.add(category);
        categoryDao.add(anotherCategory);
        categoryDao.clearAllCategories();
        assertEquals(0, categoryDao.getAll().size());
    }


}