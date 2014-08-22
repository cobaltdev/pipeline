package com.cobalt.bamboo.plugin.pipeline.cdresult;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.junit.*;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.bamboo.applinks.JiraApplinksService;
import com.atlassian.bamboo.author.Author;
import com.atlassian.bamboo.chains.ChainResultsSummary;
import com.atlassian.bamboo.commit.Commit;
import com.atlassian.bamboo.plan.Plan;
import com.atlassian.bamboo.resultsummary.ResultsSummary;
import com.atlassian.bamboo.resultsummary.ResultsSummaryManager;
import com.google.common.collect.ImmutableList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/*
The i-th (1-based index. 1 - oldest build. 0 - oldest build) build in a buildlist has i commits, 
which has authors with names "Author1", "Author2", ..., "Authori".
 */
public class SetLastDeploymentInfoTest {

	private final int LIST_SIZE = 5;
	private ChainResultsSummary ContinueSuccess;
	private ChainResultsSummary ContinueNotSuccess;
	private ChainResultsSummary NotContinueNotSuccess;
	private ChainResultsSummary NotContinueSuccess1;
	private ChainResultsSummary NotContinueSuccess2;
	private ChainResultsSummary fail2;
	private Date day1;
	private Date day2;
	ContributorBuilder cb;

	
	@Before
	public void setup(){
		// mockup JiraApplinksService
		JiraApplinksService jiraApplinksService = mock(JiraApplinksService.class);
		Iterator<ApplicationLink> itr = (Iterator<ApplicationLink>) mock(Iterator.class);
		when(itr.hasNext()).thenReturn(false);
		Iterable<ApplicationLink> iterable = (Iterable<ApplicationLink>) mock(Iterable.class);
		when(iterable.iterator()).thenReturn(itr);	
		when(jiraApplinksService.getJiraApplicationLinks()).thenReturn(iterable);
		
		cb = new ContributorBuilder(jiraApplinksService);	 
		day1 = new Date();
		day2 = new Date();
		day1.setDate(day1.getDate() - 1);
		ContinueSuccess = getChainResultsSummary(true, true, "1", day1);
		ContinueNotSuccess = getChainResultsSummary(true, false, "2", day1);
		NotContinueNotSuccess = getChainResultsSummary(false, false, "3", day1);
		fail2 = getChainResultsSummary(true, false, "6", day2);
		NotContinueSuccess1 = getChainResultsSummary(false, true, "4", day1);
		NotContinueSuccess2 = getChainResultsSummary(false, true, "5", day2);
	}
	
	@Test
	public void testTwoBuildsWithNoDeploymentCS() {
		CDResult cdr = getCDResult(fail2, ContinueSuccess);
		assertEquals("Last Deployment can't be found", null, cdr.getLastDeploymentTime());
		assertEquals("Number of Commits should be the commits of all builds", 2, cdr.getNumChanges());
		assertEquals("Number of Contributors should be contributors of all builds", 2, cdr.getContributors().size());
	}
	
	@Test
	public void testTwoBuildsWithNoDeploymentCNS() {
		CDResult cdr = getCDResult(fail2, ContinueNotSuccess);
		assertEquals("Last Deployment can't be found", null, cdr.getLastDeploymentTime());
		assertEquals("Number of Commits should be the commits of all builds", 2, cdr.getNumChanges());
		assertEquals("Number of Contributors should be contributors of all builds", 2, cdr.getContributors().size());
	}
	
	@Test
	public void testTwoBuildsWithNoDeploymentNCNS() {
		CDResult cdr = getCDResult(fail2, NotContinueNotSuccess);
		assertEquals("Last Deployment can't be found", null, cdr.getLastDeploymentTime());
		assertEquals("Number of Commits should be the commits of all builds", 2, cdr.getNumChanges());
		assertEquals("Number of Contributors should be contributors of all builds", 2, cdr.getContributors().size());
	}
	
	@Test
	public void testTwoBuildsWithOneDeployment1(){
		CDResult cdr = getCDResult(fail2, NotContinueSuccess1);
		assertEquals("Last Deployment should be the date of first build", day1, cdr.getLastDeploymentTime());
		assertEquals("Number of Commits should be the commits of second build", 1, cdr.getNumChanges());
		assertEquals("Number of Contributors should be contributors of second build", 1, cdr.getContributors().size());
	}
	
	@Test
	public void testTwoBuildsWithNoDeployment2() {
		CDResult cdr = getCDResult(NotContinueSuccess2, ContinueNotSuccess);
		assertEquals("Last Deployment can't be found", null, cdr.getLastDeploymentTime());
		assertEquals("Number of Commits should be the commits of all builds", 2, cdr.getNumChanges());
		assertEquals("Number of Contributors should be contributors of all builds", 2, cdr.getContributors().size());
	}
	
	@Test
	public void testTwoBuildsWithTwoDeployment(){
		CDResult cdr = getCDResult(NotContinueSuccess2, NotContinueSuccess1);
		assertEquals("Last Deployment should be the date of first build", day1, cdr.getLastDeploymentTime());
		assertEquals("Number of Commits should be the commits of second build", 1, cdr.getNumChanges());
		assertEquals("Number of Contributors should be contributors of second build", 1, cdr.getContributors().size());
	}
	
	// Deployment Positions: 1 being most current, 5 being oldest
	
	@Test
	public void testFiveBuildsWithTwoDeploymentsAtPos1And2() {
		testFiveBuildsWithTwoDeploymentsAtDifferentPos(1, 2, 2);
	}
	
	@Test
	public void testFiveBuildsWithTwoDeploymentsAtPos1And3() {
		testFiveBuildsWithTwoDeploymentsAtDifferentPos(1, 3, 3);
	}
	
	@Test
	public void testFiveBuildsWithTwoDeploymentsAtPos1And5() {
		testFiveBuildsWithTwoDeploymentsAtDifferentPos(1, 5, 5);
	}
	
	@Test
	public void testFiveBuildsWithTwoDeploymentsAtPos2And4() {
		testFiveBuildsWithTwoDeploymentsAtDifferentPos(2, 4, 2);
	}
	
	@Test
	public void testFiveBuildsWithTwoDeploymentsAtPos2And5() {
		testFiveBuildsWithTwoDeploymentsAtDifferentPos(2, 5, 2);
	}
	
	@Test
	public void testFiveBuildsWithTwoDeploymentsAtPos3And5() {
		testFiveBuildsWithTwoDeploymentsAtDifferentPos(3, 5, 3);
	}
	
	@Test
	public void testFiveBuildsWithThreeDeployments() {
		List<ResultsSummary> buildList = makeNormalSizeBuildList();
		for (int i = 1; i <= LIST_SIZE; i++) {
			ChainResultsSummary crs = (ChainResultsSummary) buildList.get(i - 1);
			
			if (i == 1 || i == 3 || i == 5) {  // zero-based index for 1, 3, 5
				// set to deployment condition
				when(crs.isContinuable()).thenReturn(false);
				when(crs.isSuccessful()).thenReturn(true);
			} else {
				when(crs.isContinuable()).thenReturn(true);
				when(crs.isSuccessful()).thenReturn(false);
			}
		}
		
		Plan plan = mock(Plan.class);
		ResultsSummaryManager rsMng = mockResultsSummaryManagerForBuildList(buildList, plan);
		
		CDResult cdr = new CDResult("Project", "Plan", "project", "plan");
		CDResultFactory.setLastDeploymentInfo(cdr, plan, rsMng, cb);
		
		assertEquals("Date of last deployment doesn't match.", 
						(new Date(3)).getTime(), cdr.getLastDeploymentTime().getTime());
		assertEquals("Number of changes since last deployment doesn't match.", 
						3, cdr.getNumChanges());
		assertEquals("Number of contributors since last deployment doesn't match.", 
						2, cdr.getContributors().size());
	}
	
	@Test
	public void testFiveBuildsWithAllBeingDeployment() {
		List<ResultsSummary> buildList = makeNormalSizeBuildList();
		
		// set all build to deployment condition
		for (int i = 1; i <= LIST_SIZE; i++) {
			ChainResultsSummary crs = (ChainResultsSummary) buildList.get(i - 1);
			when(crs.isContinuable()).thenReturn(false);
			when(crs.isSuccessful()).thenReturn(true);
		}
		
		Plan plan = mock(Plan.class);
		ResultsSummaryManager rsMng = mockResultsSummaryManagerForBuildList(buildList, plan);
		
		CDResult cdr = new CDResult("Project", "Plan", "project", "plan");
		CDResultFactory.setLastDeploymentInfo(cdr, plan, rsMng, cb);
		
		assertEquals("Date of last deployment doesn't match.", 
						(new Date(2)).getTime(), cdr.getLastDeploymentTime().getTime());
		assertEquals("Number of changes since last deployment doesn't match.", 
						1, cdr.getNumChanges());
		assertEquals("Number of contributors since last deployment doesn't match.", 
						1, cdr.getContributors().size());
	}
	
	// no build 
	@Test 
	public void testNoBuild() {
		List<ResultsSummary> emptyBuildList = new ArrayList<ResultsSummary>();
		Plan plan = mock(Plan.class);
		ResultsSummaryManager rsMng = mockResultsSummaryManagerForBuildList(emptyBuildList, plan);
		
		CDResult cdr = new CDResult("Project", "Plan", "project", "plan");
		CDResultFactory.setLastDeploymentInfo(cdr, plan, rsMng, cb);
		
		assertEquals("No build should have no change.", 0, cdr.getNumChanges());
		assertEquals("No build should have no contributor.", new HashSet<Contributor>(), cdr.getContributors());
		assertEquals("No build should have no last deployment date.", null, cdr.getLastDeploymentTime());
	}
			

	// 1 build (succ/fail). No last deployment
	@Test
	public void testOneBuildSuccessful() {
		List<ResultsSummary> buildList = createBuildList(1, 0);
		Plan plan = mock(Plan.class);
		ResultsSummaryManager rsMng = mockResultsSummaryManagerForBuildList(buildList, plan);
		
		CDResult cdr = new CDResult("Project", "Plan", "project", "plan");
		CDResultFactory.setLastDeploymentInfo(cdr, plan, rsMng, cb);
			
		assertEquals("Build with size 1 should have 1 change.", 1, cdr.getNumChanges());		
		assertEquals("Build with size 1  should have 1 contributor.", 1, cdr.getContributors().size());
		assertEquals("Build with size 1  should have no last deployment date", null, cdr.getLastDeploymentTime());	
	}
		
	@Test
	public void testOneBuildFailed() {
		List<ResultsSummary> buildList = createBuildList(1, -1);
		Plan plan = mock(Plan.class);
		ResultsSummaryManager rsMng = mockResultsSummaryManagerForBuildList(buildList, plan);
		
		CDResult cdr = new CDResult("Project", "Plan", "project", "plan");
		CDResultFactory.setLastDeploymentInfo(cdr, plan, rsMng, cb);
		
		assertEquals("Build with size 1 should have 1 change.", 1, cdr.getNumChanges());		
		assertEquals("Build with size 1 should have 1 contributor.", 1, cdr.getContributors().size());
		assertEquals("Build with size 1 should have no last deployment date", null, cdr.getLastDeploymentTime());
	}
		
		
	// 5 builds (first 2 cases)
	@Test
	public void testFiveBuildsNoDeployment() {
		// N N N N N        (Is deployment? Y/N)
		List<ResultsSummary> buildList = createBuildList(5, -1);
		Plan plan = mock(Plan.class);
		ResultsSummaryManager rsMng = mockResultsSummaryManagerForBuildList(buildList, plan);
		
		CDResult cdr = new CDResult("Project", "Plan", "project", "plan");
		CDResultFactory.setLastDeploymentInfo(cdr, plan, rsMng, cb);
		
		assertEquals("Buildlist with no deployment should add up all changes", 15, cdr.getNumChanges());		
		assertEquals("Buildlist with no deployment should add up all contributors", 5, cdr.getContributors().size());
		assertEquals("Buildlist with no deployment should have no deployment date.", null, cdr.getLastDeploymentTime());	
	}
		
	@Test
	public void testFiveBuildsOneNewestDeployment() {
		// N N N N Y
		List<ResultsSummary> buildList = createBuildList(5, 0);
		Plan plan = mock(Plan.class);
		ResultsSummaryManager rsMng = mockResultsSummaryManagerForBuildList(buildList, plan);
		
		CDResult cdr = new CDResult("Project", "Plan", "project", "plan");
		CDResultFactory.setLastDeploymentInfo(cdr, plan, rsMng, cb);
		
		assertEquals("Buildlist with the last build as deployment should add up all changes.", 15, cdr.getNumChanges());		
		assertEquals("Buildlist with the last build as deployment should add up all contributors.", 5, cdr.getContributors().size());
		assertEquals("Buildlist with the last build as deployment should have no deployment date.", null, cdr.getLastDeploymentTime());	
	}
		
	@Test
	public void testFiveBuildsSecondNewestDeployment() {
		// N N N Y N
		List<ResultsSummary> buildList = createBuildList(5, 1);
		Plan plan = mock(Plan.class);
		ResultsSummaryManager rsMng = mockResultsSummaryManagerForBuildList(buildList, plan);
		
		CDResult cdr = new CDResult("Project", "Plan", "project", "plan");
		CDResultFactory.setLastDeploymentInfo(cdr, plan, rsMng, cb);
			
		assertEquals("Should only count the changes in the newest build", 1, cdr.getNumChanges());		
		assertEquals("Should only count the contributors in the newest build", 1, cdr.getContributors().size());
		assertEquals("Should be the date of the 2nd newest build", new Date(2014, 1, 1, 1, 1, 1), cdr.getLastDeploymentTime());
	}
		

	@Test
	public void testFiveBuildsMidDeployment() {
		// N N Y N N
		List<ResultsSummary> buildList = createBuildList(5, 2);
		Plan plan = mock(Plan.class);
		ResultsSummaryManager rsMng = mockResultsSummaryManagerForBuildList(buildList, plan);
		
		CDResult cdr = new CDResult("Project", "Plan", "project", "plan");
		CDResultFactory.setLastDeploymentInfo(cdr, plan, rsMng, cb);
		
		assertEquals(3, cdr.getNumChanges());		
		assertEquals(2, cdr.getContributors().size());
		assertEquals(new Date(2014, 1, 1, 1, 1, 1), cdr.getLastDeploymentTime());	
	}
		
	@Test
	public void testFiveBuildsOldestDeployment() {
		// Y N N N N 
		List<ResultsSummary> buildList = createBuildList(5, 4);
		Plan plan = mock(Plan.class);
		ResultsSummaryManager rsMng = mockResultsSummaryManagerForBuildList(buildList, plan);
		
		CDResult cdr = new CDResult("Project", "Plan", "project", "plan");
		CDResultFactory.setLastDeploymentInfo(cdr, plan, rsMng, cb);
			
		assertEquals("Should add up #changes up to the oldest build.", 10, cdr.getNumChanges());		
		assertEquals("Should add the contributors up to the oldest build.", 4, cdr.getContributors().size());
		assertEquals("Should be the date of the oldest build", new Date(2014, 1, 1, 1, 1, 1), cdr.getLastDeploymentTime());	
	}
	
	@Test
	public void test50BuildsWithRandomDeploymentPosition1(){
		List<ResultsSummary> buildList = createBuildList(50, 49);
		Plan plan = mock(Plan.class);
		ResultsSummaryManager rsMng = mockResultsSummaryManagerForBuildList(buildList, plan);
		
		CDResult cdr = new CDResult("Project", "Plan", "project", "plan");
		CDResultFactory.setLastDeploymentInfo(cdr, plan, rsMng, cb);
			
		assertEquals("Should add up #changes up to the oldest build.", 1225, cdr.getNumChanges());		
		assertEquals("Should add the contributors up to the oldest build.", 49, cdr.getContributors().size());
		assertEquals("Should be the date of the oldest build", new Date(2014, 1, 1, 1, 1, 1), cdr.getLastDeploymentTime());	
	}
	
	@Test
	public void test50BuildsWithRandomDeploymentPosition2(){
		List<ResultsSummary> buildList = createBuildList(50, 25);
		Plan plan = mock(Plan.class);
		ResultsSummaryManager rsMng = mockResultsSummaryManagerForBuildList(buildList, plan);
		
		CDResult cdr = new CDResult("Project", "Plan", "project", "plan");
		CDResultFactory.setLastDeploymentInfo(cdr, plan, rsMng, cb);
			
		assertEquals("Should add up #changes up to the oldest build.", 325, cdr.getNumChanges());		
		assertEquals("Should add the contributors up to the oldest build.", 25, cdr.getContributors().size());
		assertEquals("Should be the date of the oldest build", new Date(2014, 1, 1, 1, 1, 1), cdr.getLastDeploymentTime());	
	}
	
	@Test
	public void test50BuildsWithRandomDeploymentPosition3(){
		List<ResultsSummary> buildList = createBuildList(50, 30);
		Plan plan = mock(Plan.class);
		ResultsSummaryManager rsMng = mockResultsSummaryManagerForBuildList(buildList, plan);
		
		CDResult cdr = new CDResult("Project", "Plan", "project", "plan");
		CDResultFactory.setLastDeploymentInfo(cdr, plan, rsMng, cb);
			
		assertEquals("Should add up #changes up to the oldest build.", 465, cdr.getNumChanges());		
		assertEquals("Should add the contributors up to the oldest build.", 30, cdr.getContributors().size());
		assertEquals("Should be the date of the oldest build", new Date(2014, 1, 1, 1, 1, 1), cdr.getLastDeploymentTime());	
	}
	
	// ========== Private Helper Methods ==========
	
	private ResultsSummaryManager mockResultsSummaryManagerForBuildList(List<ResultsSummary> buildList, Plan plan){
		ResultsSummaryManager rsMng = mock(ResultsSummaryManager.class);
		when(rsMng.getResultSummariesForPlan(plan, 0, 1)).thenReturn(buildList.subList(0, Math.min(1, buildList.size())));
		for(int i = 1; i < buildList.size(); i+=10){
			when(rsMng.getResultSummariesForPlan(plan, i, 10)).thenReturn(buildList.subList(i, Math.min(i + 10, buildList.size())));
		}
		return rsMng;
	}
	
	private void testFiveBuildsWithTwoDeploymentsAtDifferentPos(int pos1, int pos2, int expectedPos) {
		List<ResultsSummary> buildList = makeNormalSizeBuildList();
		setTwoDeployments(buildList, pos1, pos2);
		
		Plan plan = mock(Plan.class);
		ResultsSummaryManager rsMng = mockResultsSummaryManagerForBuildList(buildList, plan);
		CDResult cdr = new CDResult("Project", "Plan", "project", "plan");
		CDResultFactory.setLastDeploymentInfo(cdr, plan, rsMng, cb);
		
		// Last deployment should be second one in the buildList
		
		int expectedChanges = 0;
		for (int i = 1; i < expectedPos; i++) {
			expectedChanges += i;
		}
		
		assertEquals("Date of last deployment doesn't match.", 
						(new Date(expectedPos)).getTime(), cdr.getLastDeploymentTime().getTime());
		assertEquals("Number of changes since last deployment doesn't match.", 
						expectedChanges, cdr.getNumChanges());
		assertEquals("Number of contributors since last deployment doesn't match.", 
						expectedPos - 1, cdr.getContributors().size());
	}
	
	private List<ResultsSummary> makeNormalSizeBuildList() {
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		for (int i = 1; i <= LIST_SIZE; i++) {
			ChainResultsSummary crs = mock(ChainResultsSummary.class);
			
			ImmutableList.Builder<Commit> builder = new ImmutableList.Builder<Commit>();
			for (int j = 1; j <= i; j++) {
				Commit c = mock(Commit.class);
				when(c.getComment()).thenReturn("comment");
				Author a = mock(Author.class);
				when(a.getLinkedUserName()).thenReturn("Author" + j);
				when(a.getName()).thenReturn("Author" + j);
				when(c.getAuthor()).thenReturn(a);
				when(c.getDate()).thenReturn(new Date());
				builder.add(c);
			}
			
			when(crs.getCommits()).thenReturn(builder.build());
			when(crs.getBuildCompletedDate()).thenReturn(new Date(i));
			
			buildList.add(crs);
		}
		
		return buildList;
	}
	
	// pos1, pos2: the indexes of the deployments to be set at (1 - most current, 5 - oldest)
	private void setTwoDeployments(List<ResultsSummary> list, int pos1, int pos2) {		
		for (int i = 1; i <= LIST_SIZE; i++) {
			ChainResultsSummary crs = (ChainResultsSummary) list.get(i - 1);
			
			if (i == pos1 || i == pos2) {
				// set to deployment condition
				when(crs.isContinuable()).thenReturn(false);
				when(crs.isSuccessful()).thenReturn(true);
			} else {
				when(crs.isContinuable()).thenReturn(true);
				when(crs.isSuccessful()).thenReturn(false);
			}
		}
	}
	
	private ChainResultsSummary getChainResultsSummary(boolean cont, boolean succ, String name, Date date){
		ChainResultsSummary result = mock(ChainResultsSummary.class);
		when(result.isContinuable()).thenReturn(cont);
		when(result.isSuccessful()).thenReturn(succ);
		Commit c = mock(Commit.class);
		when(c.getComment()).thenReturn("comment");
		Author a = mock(Author.class);
		when(a.getLinkedUserName()).thenReturn(name);
		when(a.getName()).thenReturn(name);
		when(c.getAuthor()).thenReturn(a);
		when(c.getDate()).thenReturn(new Date());
		ImmutableList<Commit> commits = ImmutableList.of(c);
		when(result.getCommits()).thenReturn(commits);
		when(result.getBuildCompletedDate()).thenReturn(date);
		when(result.getBuildNumber()).thenReturn(0); // TODO
		return result;
	}
	
	private CDResult getCDResult(ResultsSummary rs1, ResultsSummary rs2){
		List<ResultsSummary> builds = new ArrayList<ResultsSummary>();
		builds.add(rs1);
		builds.add(rs2);
		
		Plan plan = mock(Plan.class);
		ResultsSummaryManager rsMng = mockResultsSummaryManagerForBuildList(builds, plan);
		CDResult cdr = new CDResult("test", "test", "test", "test");
		CDResultFactory.setLastDeploymentInfo(cdr, plan, rsMng, cb);
		return cdr;
	}
	
	// create a list of commits, each w/ its authors' full name
	private ImmutableList<Commit> createCommitList(int numCommits) {
		ImmutableList.Builder<Commit> commits = new ImmutableList.Builder <Commit>();
		for (int i = 0; i < numCommits; i++) {
			Author author = mock(Author.class);
		    when(author.getLinkedUserName()).thenReturn("author" + i);
		    when(author.getName()).thenReturn("author" + i);
			Commit commit = mock(Commit.class);
		    when(commit.getAuthor()).thenReturn(author);
		    when(commit.getDate()).thenReturn(new Date());
		    when(commit.getComment()).thenReturn("comment");
		    commits.add(commit);
		}
		return commits.build();
	}
		
	// create a build that is a deployment (!C & S)
	private ResultsSummary createDeploymentBuild() {
		ChainResultsSummary build = mock(ChainResultsSummary.class);
		// set its deployment condition 
		when(build.isContinuable()).thenReturn(false);
		when(build.isSuccessful()).thenReturn(true);
		when(build.getBuildCompletedDate()).thenReturn(new Date(2014, 1, 1, 1, 1, 1));
			
		return build;
	}
	
		// create a build that is not a deployment 
	// randomly assign one of the 3 failure cases to it (C & S, C & !S, !C & !S)
	private ResultsSummary createNonDeploymentBuild() {
		ChainResultsSummary build = mock(ChainResultsSummary.class);
		Random r = new Random();
		int failCaseNum = r.nextInt(3); // randomly choose one of the failure cases
			
		// set its deployment condition 
		if (failCaseNum == 0) { // C & S
			when(build.isContinuable()).thenReturn(true);
			when(build.isSuccessful()).thenReturn(true);
			} else if (failCaseNum == 1) { // C & !S
			when(build.isContinuable()).thenReturn(true);
			when(build.isSuccessful()).thenReturn(false);
		} else { // !C & !S
			when(build.isContinuable()).thenReturn(false);
			when(build.isSuccessful()).thenReturn(false);
		}
			
		return build;
	}
		
	// create a list of builds with the specified position of deployment (-1 if no deployment)
	private List<ResultsSummary> createBuildList(int numBuilds, int deploymentIndex) {
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		for (int i = 0; i < numBuilds; i++) {
			// create a deployment/non-deployment build
			ResultsSummary build;	
			if (i == deploymentIndex) {
				build = createDeploymentBuild();
			} else {
				build = createNonDeploymentBuild();
			}		
			// add the build to the list
			buildList.add(build);

			// associated the build with a list of commits
			ImmutableList<Commit> commits = createCommitList(i + 1);	 // add 1	
			when(build.getCommits()).thenReturn(commits);
		}
		return buildList;
	}
}
