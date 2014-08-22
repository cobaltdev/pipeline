package com.cobalt.bamboo.plugin.pipeline.changelist;

import org.junit.Test;
import static org.junit.Assert.*;

public class CommentModifierTest {
	
	@Test
	public void testImportedFromGitPerforce() {
		String commitMsg = "Finished unit test Imported from Git Author: admin <admin@abc.com> 1498873298 -923 Committer: admin <admim@abc.com> 1498873298 -0923 sha1: 8998b9c8789a78823202912i3293892ab732d push-state: complete parent-changes: 9723aj0a8s98233kmcssi8298s9b0a89932=[3287322]";
		String cleanMsg = CommentModifier.removeVersionControlInfo(commitMsg);
		assertEquals("Finished unit test ", cleanMsg);
	}

	@Test
	public void testImportedFromOtherVCS() {
		String commitMsg = "Finished unit test";
		String cleanMsg = CommentModifier.removeVersionControlInfo(commitMsg);
		assertEquals(commitMsg, cleanMsg);
	}
}
