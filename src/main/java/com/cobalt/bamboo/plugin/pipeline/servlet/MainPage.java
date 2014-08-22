package com.cobalt.bamboo.plugin.pipeline.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.cobalt.bamboo.plugin.pipeline.Controllers.CacheManager;
import com.cobalt.bamboo.plugin.pipeline.Controllers.MainManager;
import com.cobalt.bamboo.plugin.pipeline.cache.WallBoardData;
import com.cobalt.bamboo.plugin.pipeline.cdperformance.CDPerformance;
import com.cobalt.bamboo.plugin.pipeline.changelist.Change;

public class MainPage extends HttpServlet{
	private static final Logger log = LoggerFactory.getLogger(MainPage.class);
    private final UserManager userManager;
    private final LoginUriProvider loginUriProvider;
    private final TemplateRenderer renderer;
    private final CacheManager cacheManager;
    private final MainManager mainManager;
   
    public MainPage(UserManager userManager, LoginUriProvider loginUriProvider,  TemplateRenderer renderer,
    				CacheManager cacheManager, MainManager mainManager)
    {
      this.userManager = userManager;
      this.loginUriProvider = loginUriProvider;
      this.renderer = renderer;
      this.cacheManager = cacheManager;
      this.mainManager = mainManager;
    }
   
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {

      // Redirect the user if user is not admin
	  String username = userManager.getRemoteUsername(request);
	  if (username == null)
	  {
	    redirectToLogin(request, response);
	    return;
	  }
	  
	  String query = request.getParameter("data");
	  
	  if (query == null) {  // TODO
		  // Normal case: normal table page
		  response.setContentType("text/html;charset=utf-8");
		  renderer.render("cdpipeline.vm", response.getWriter());
	  } else if (query.equalsIgnoreCase("all")) {
		  // Special Case: JSON request
		  ObjectWriter writer = (new ObjectMapper()).writer().withDefaultPrettyPrinter();
		  List<WallBoardData> resultList = cacheManager.getAllWallBoardData();
		  String json = writer.writeValueAsString(resultList);
		  response.setContentType("application/json;charset=utf-8");
		  response.getWriter().write(json);
	  } else if (query.equalsIgnoreCase("changes") && request.getParameter("plankey") != null){
		  List<Change> changeList = mainManager.getChangeListForPlan(request.getParameter("plankey"));
		  ObjectWriter writer = (new ObjectMapper()).writer().withDefaultPrettyPrinter();
		  String json = writer.writeValueAsString(changeList);
		  response.setContentType("application/json;charset=utf-8");
		  response.getWriter().write(json);
	  } else if (query.equalsIgnoreCase("completions") && request.getParameter("plankey") != null){
		  CDPerformance performance = mainManager.getPerformanceStatsForPlan(request.getParameter("plankey"));
		  ObjectWriter writer = (new ObjectMapper()).writer().withDefaultPrettyPrinter();
		  String json = writer.writeValueAsString(performance);
		  response.setContentType("application/json;charset=utf-8");
		  response.getWriter().write(json);
	  } else{
		  response.setContentType("text/html;charset=utf-8");
		  renderer.render("cdpipeline.vm", response.getWriter());
	  }
    }
    
    private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
      response.sendRedirect(loginUriProvider.getLoginUri(getUri(request)).toASCIIString());
    }
      
    private URI getUri(HttpServletRequest request)
    {
      StringBuffer builder = request.getRequestURL();
      if (request.getQueryString() != null)
      {
        builder.append("?");
        builder.append(request.getQueryString());
      }
      return URI.create(builder.toString());
    }

}
