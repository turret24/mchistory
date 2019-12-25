package com.boulder.mchistory.basic;

import com.boulder.mchistory.daos.PostDao;
import com.boulder.mchistory.objects.Post;
import com.boulder.mchistory.objects.Result;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "HomePage", urlPatterns = {"", "/Home"}, loadOnStartup = 1)
@SuppressWarnings("serial")
public class HomePage extends HttpServlet {

  private static final Logger logger = Logger.getLogger(HomePage.class.getName());

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
    PostDao dao = (PostDao) this.getServletContext().getAttribute("postDao");
    String startCursor = req.getParameter("cursor");
    List<Post> posts = null;
    String endCursor = null;

    try {
      Result<Post> result = dao.listPosts(startCursor);
      logger.log(Level.INFO, "Retrieved list of all posts");
      posts = result.result;
      endCursor = result.cursor;
      logger.info("Home Page endCursor set to " + endCursor);
    } catch (Exception e) {
      throw new ServletException("Error listing posts", e);
    }
    
    StringBuilder postNames = new StringBuilder();

    for (Post post : posts) {
      postNames.append(post.getTitle() + " ");
      
      if (post.getInfo().length() > 200) {
    	  StringBuilder str = new StringBuilder(post.getInfo());
    	  str.setLength(200);
    	  str.append(" ...");
    	  post.setInfo(str.toString());
      }
    }

    req.setAttribute("posts", posts);
    req.setAttribute("cursor", endCursor);;
    req.setAttribute("page", "home-page");
    req.getRequestDispatcher("/base.jsp").forward(req, resp);
  }

}