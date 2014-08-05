package ut.com.cobalt.cdpipeline.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.mockito.Mockito;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.bamboo.applinks.JiraApplinksService;
import com.atlassian.bamboo.plan.PlanExecutionManager;
import com.atlassian.bamboo.plan.PlanManager;
import com.atlassian.bamboo.resultsummary.ResultsSummaryManager;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.cobalt.bamboo.plugin.pipeline.servlet.MainPage;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MainPageTest {

	HttpServletRequest mockRequest;
    HttpServletResponse mockResponse;
    MainPage main;

    // this test is auto-generated upon creation of the plugin
    @Before
    public void setup() {
        mockRequest = mock(HttpServletRequest.class);
        mockResponse = mock(HttpServletResponse.class);
        setUpMainPage();
    }
    
    // this test is auto-generated upon creation of the plugin
    @After
    public void tearDown() {

    }

    // this test is auto-generated upon creation of the plugin
    @Test
    public void testSomething() {
        String expected = "test";
        when(mockRequest.getParameter(Mockito.anyString())).thenReturn(expected);
        assertEquals(expected,mockRequest.getParameter("some string"));

    }
    
    @Test
    public void testDoGetWithNoURLParam() {
    	testDoGetWithDifferentURLParam(null, "text/html;charset=utf-8");
    }
    
    @Test
    public void testDoGetWithRandomURLParam() {
    	testDoGetWithDifferentURLParam("apple", "text/html;charset=utf-8");
    }
    
    @Test
    public void testDoGetWithURLParamJsonAllLowerCase() {
    	testDoGetWithDifferentURLParam("json", "application/json;charset=utf-8");
    }
    
    @Test
    public void testDoGetWithURLParamJsonAllUpperCase() {
    	testDoGetWithDifferentURLParam("JSON", "application/json;charset=utf-8");
    }
    
    @Test
    public void testDoGetWithURLParamJsonRandomCase() {
    	testDoGetWithDifferentURLParam("jSoN", "application/json;charset=utf-8");
    }
    
    // ========== Private Helper Methods ==========
    
    private void testDoGetWithDifferentURLParam(String returnParam, String expected) {
    	when(mockRequest.getParameter("type")).thenReturn(returnParam);
    	try {
			main.doGet(mockRequest, mockResponse);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServletException e) {
			e.printStackTrace();
		}
    	verify(mockResponse).setContentType(eq(expected));
    }
    
    private void setUpMainPage() {
    	try {
    		PrintWriter pw = mock(PrintWriter.class);
			when(mockResponse.getWriter()).thenReturn(pw);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	// Set up UserManager used in MainPage
    	UserManager userMgr = mock(UserManager.class);
    	when(userMgr.getRemoteUsername(mockRequest)).thenReturn("admin");
    	when(userMgr.isSystemAdmin("admin")).thenReturn(true);
    	
    	// Set up JiraApplinksService used in ContributorBuilder
    	JiraApplinksService jiraApplinks = mock(JiraApplinksService.class);
		Iterable<ApplicationLink> applinks = (Iterable<ApplicationLink>) mock(Iterable.class);
    	when(jiraApplinks.getJiraApplicationLinks()).thenReturn(applinks);
		Iterator<ApplicationLink> applinksIter = (Iterator<ApplicationLink>) mock(Iterator.class);
    	when(applinks.iterator()).thenReturn(applinksIter);
    	when(applinksIter.hasNext()).thenReturn(false);
    	
    	main = new MainPage(userMgr, mock(LoginUriProvider.class), mock(TemplateRenderer.class), 
    			mock(PlanManager.class), mock(ResultsSummaryManager.class), 
    			jiraApplinks, mock(PlanExecutionManager.class));
    }
}
