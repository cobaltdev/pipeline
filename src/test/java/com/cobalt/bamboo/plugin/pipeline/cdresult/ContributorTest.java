package com.cobalt.bamboo.plugin.pipeline.cdresult;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

public class ContributorTest {
	Contributor super1, super2, super3;
	Contributor test1, test2;
	Date current;
    	
    @Before
    public void setup() {
        super1 = new Contributor("super pipe", new Date(), null, null, null);
        super2 = new Contributor("Super Pipe", new Date(), null, null, null);
        super3 = new Contributor("SUPER PIPE", new Date(), null, null, null);
        current = new Date();
        test1 = new Contributor("test 1", current, null, null, null);
        test2 = new Contributor("test 2", new Date(current.getTime() - 10000), null, null, null);
    }

	// test equals()
	
	@Test
	public void test_equals_same_name() {
        Contributor superfake = new Contributor("super pipe", new Date(), null, null, null);
		assertTrue(super1.equals(superfake));
	}
	
	@Test
	public void test_equals_unique_names() {
        Contributor anotherUser = new Contributor("super pie", new Date(), null, null, null);
		assertFalse(super1.equals(anotherUser));
	}
	
	@Test
	public void test_unique_names_same_chars() {
        assertFalse(super1.equals(super2));
        assertFalse(super1.equals(super3));
	}
	
	// test hashcode()
	
    @Test
	public void test_hash_same_name() {
        Contributor superfake = new Contributor("super pipe", new Date(), null, null, null);
		assertEquals(super1, superfake);
	}
	
	@Test
	public void test_hash_unique_names() {
        Contributor anotherUser = new Contributor("old pie", new Date(), null, null, null);
        assertTrue(super1 != anotherUser);

	}
	
	@Test
	public void test_hash_names_same_chars() {
        assertTrue(super1 != super2);
        assertTrue(super1 != super3);
	}
	
	// test update last commit time
	@Test
	public void test_update_last_commit_time_change(){
		test2.updateLastCommitTime(current);
		assertEquals("Last Commit Time shoud be updated", current, test2.getLastCommitTime());
	}
	
	@Test
	public void test_update_last_commit_time_unchange(){
		Date d = new Date(current.getTime() - 10000);
		test1.updateLastCommitTime(d);
		assertEquals("Last Commit Time should not be updated", current, test1.getLastCommitTime());
	}
	
	// test number of commits
	@Test
	public void test_increment_commits(){
		assertEquals("Initial commit number should be 1", 1, test1.getCommitCount());
		test1.incrementCommitCount();
		assertEquals("Number of Commit should be incremented", 2, test1.getCommitCount());
	}
}
