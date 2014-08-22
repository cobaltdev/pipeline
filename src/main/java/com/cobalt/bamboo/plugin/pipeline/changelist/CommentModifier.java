package com.cobalt.bamboo.plugin.pipeline.changelist;

public class CommentModifier {
	private static final String PERFORCE_MESSAGE = "Imported from Git";
	
	/**
	 * Remove the VCS-specific message from the comment of code commit
	 * @param comment The original code commit message
	 * @return comment with VCS message removed
	 */
	public static String removeVersionControlInfo(String comment) {
		int vcInfo = comment.indexOf(PERFORCE_MESSAGE);
		if (vcInfo != -1) {
			return comment = comment.substring(0, vcInfo);
		} else {
			return comment;
		}
	}
}
