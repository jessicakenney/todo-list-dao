import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.Sql2oCategoryDao;
import dao.Sql2oTaskDao;
import dao.TaskDao;
import models.Category;
import models.Task;
import org.sql2o.Sql2o;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;
import static spark.Spark.*;

public class App {
    public static void main(String[] args) { //type “psvm + tab” to autocreate this
        staticFileLocation("/public");

        String connectionString = "jdbc:h2:~/todolist.db;INIT=RUNSCRIPT from 'classpath:db/create.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        Sql2oTaskDao taskDao = new Sql2oTaskDao(sql2o);
        Sql2oCategoryDao categoryDao = new Sql2oCategoryDao(sql2o);


        //get: delete all categories
        get("/categories/delete", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            categoryDao.clearAllCategories();
            return new ModelAndView(model, "success.hbs");
        }, new HandlebarsTemplateEngine());

        //get: show all Categories
        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Category> categories = categoryDao.getAll();
            model.put("categories", categories);
            return new ModelAndView(model, "index.hbs");
        }, new HandlebarsTemplateEngine());

        //get: show new Category form
        get("/categories/new", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            return new ModelAndView(model, "category-form.hbs");
        }, new HandlebarsTemplateEngine());

        //task: process new Category form
        post("/categories/new", (request, response) -> { //URL to make new task on POST route
            Map<String, Object> model = new HashMap<>();
            String name = request.queryParams("name");
            Category newCategory = new Category(name);
            categoryDao.add(newCategory);
            model.put("category", newCategory);
            return new ModelAndView(model, "success.hbs");
        }, new HandlebarsTemplateEngine());

        //get:show individual Category
        get("/categories/:category_id", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfCategory = Integer.parseInt(req.params("category_id"));
            Category foundCategory = categoryDao.findById(idOfCategory);
            model.put("category", foundCategory);
            return new ModelAndView(model, "category-detail.hbs");
        }, new HandlebarsTemplateEngine());

        //get: update and individual Category
        get("/categories/:category_id/update", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfCategoryToEdit = Integer.parseInt(req.params("category_id"));
            Category editCategory = categoryDao.findById(idOfCategoryToEdit);
            model.put("editCategory", editCategory);
            return new ModelAndView(model, "category-form.hbs");
        }, new HandlebarsTemplateEngine());

        //post: process individual category edit
        post("/categories/:category_id/update", (req, res) -> { //URL to make new task on POST route
            Map<String, Object> model = new HashMap<>();
            String newName = req.queryParams("name");
            int categoryId = Integer.parseInt(req.params("category_id"));
            categoryDao.findById(categoryId);
            categoryDao.update(categoryId,newName);
            return new ModelAndView(model, "success.hbs");
        }, new HandlebarsTemplateEngine());

        //get: delete an Individual category
        get("/categories/:category_id/delete", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfCategoryToDelete = Integer.parseInt(req.params("category_id")); //pull id - must match route segment
            categoryDao.findById(idOfCategoryToDelete); //use it to find task
            categoryDao.deleteById(idOfCategoryToDelete);
            return new ModelAndView(model, "success.hbs");
        }, new HandlebarsTemplateEngine());

        //get: show new task form
        get("/categories/:category_id/tasks", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int categoryId = Integer.parseInt(req.params("category_id"));
            model.put("categoryId",categoryId);
            Category category = categoryDao.findById(categoryId);
            String categoryName = category.getName();
            model.put("categoryName",categoryName);
            List<Task>categoryTasks = new ArrayList<>();
            List<Task> tasks = taskDao.getAll();
            for ( Task task : tasks) {
                if (task.getCategoryId() == categoryId) {
                    categoryTasks.add(task);
                }
            }
            model.put("tasks", categoryTasks);
            return new ModelAndView(model, "task-form.hbs");
        }, new HandlebarsTemplateEngine());

        //task: process new task form
        post("/categories/:category_id/tasks", (request, response) -> { //URL to make new task on POST route
            Map<String, Object> model = new HashMap<>();
            String description = request.queryParams("description");
            int categoryId = Integer.parseInt(request.params("category_id"));
            Task newTask = new Task(description,categoryId);
            taskDao.add(newTask);
            model.put("task", newTask);
            return new ModelAndView(model, "success.hbs");
        }, new HandlebarsTemplateEngine());


        //get: show an individual task that is nested in a category
        get("/categories/:category_id/tasks/:task_id", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfTaskToFind = Integer.parseInt(req.params("task_id"));
            Task foundTask = taskDao.findById(idOfTaskToFind);
            int categoryId = Integer.parseInt(req.params("category_id"));
            model.put("task", foundTask);
            model.put("categoryId",categoryId);
            return new ModelAndView(model, "task-detail.hbs");
        }, new HandlebarsTemplateEngine());


        //get: show a form to update a task
        get("/categories/:category_id/tasks/:task_id/update", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfTaskToEdit = Integer.parseInt(req.params("task_id"));
            Task editTask = taskDao.findById(idOfTaskToEdit);
            int categoryId = Integer.parseInt(req.params("category_id"));
            model.put("editTask", editTask);
            model.put("categoryId",categoryId);
            return new ModelAndView(model, "task-form.hbs");
        }, new HandlebarsTemplateEngine());

        //task: process a form to update a task
        post("/categories/:category_id/tasks/:task_id/update", (req, res) -> { //URL to make new task on POST route
            Map<String, Object> model = new HashMap<>();
            String newContent = req.queryParams("description");
            int idOfTaskToEdit = Integer.parseInt(req.params("task_id"));
            taskDao.findById(idOfTaskToEdit);
            int categoryId = Integer.parseInt(req.params("category_id"));
            taskDao.update(idOfTaskToEdit,newContent,categoryId);
            return new ModelAndView(model, "success.hbs");
        }, new HandlebarsTemplateEngine());

        //get: delete an individual task
        get("/categories/:category_id/tasks/:task_id/delete", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfTaskToDelete = Integer.parseInt(req.params("task_id")); //pull id - must match route segment
            taskDao.findById(idOfTaskToDelete); //use it to find task
            taskDao.deleteById(idOfTaskToDelete);
            return new ModelAndView(model, "success.hbs");
        }, new HandlebarsTemplateEngine());


    }
}
