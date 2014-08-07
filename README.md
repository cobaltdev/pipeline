Pipeline - Atlassian Bamboo Plugin
========

Real-time pipeline wallboard that provides an intuitive view and encourages continuous delivery practices.

### Requirements

##### Environment Requirements
<ul>
<li>Bamboo version: 5.5.1 or higher</li>
<li>Supported browser: 
<ul>
<li>Chrome 36.0 or higher</li>
<li>Firefox 29.0 or higher</li>
<li>Safari *  7.0 or higher for mac</li>
<li>Internet Explorer 9.0 or higher</li>
</ul></li>
</ul>

##### Other Requirements
* To see contributor's profile picture:
<ol>
<li>Create an application link to JIRA. Be sure to make your JIRA application link primary if you have multiple application links. For more instruction on creating application links, click [here](https://confluence.atlassian.com/display/BAMBOO/Linking+to+another+application).<br>
**Important:**  Users must have the same username (case sensitive) in both Bamboo and JIRA in order to display the correct profile picture.</li>
<li>Link authors to users in Bamboo. Click [here](https://confluence.atlassian.com/display/BAMBOO/Managing+authors) for more information.</li>
<li>Make sure that you log in to JIRA while you're viewing the Pipeline board.</li>
</ol>

### Installation Instructions
<ol>
<li>Log into your Bamboo instance as an admin.</li>
<li>Click the admin dropdown and choose Atlassian Marketplace.
The Manage add-ons screen loads.</li>
<li>Click Find new add-ons from the left-hand side of the page.</li>
<li>Locate Pipeline Plugin - Bamboo via search.</li>
The appropriate add-on version appears in the search results.
<li>Click Install to download and install your add-on.</li>
<li>Create an application link to JIRA. Be sure to make your JIRA application link primary if you have multiple application links. See <a href="#other-requirements">Other Requirements</a> above for more details.</li>
<li>You're all set!
Click Close in the Installed and ready to go dialog.</li>
</ol>

### Development Instructions
<ol>
<li>
	Install the Atlassian Software Development Kit (SDK) and set up your stand-alone instance <br>
	- <a href="https://developer.atlassian.com/display/DOCS/Set+up+the+Atlassian+Plugin+SDK+and+Build+a+Project"> Set up Atlassian Plugin SDK </a> <br>
	(Make sure you use "atlas-run-standalone --product bamboo" instead of "atlas-run-standalone --product jira")
</li>
<li>In the same directory as your stand-alone instance, download the plugin <br>
>> `git clone https://github.com/cobaltdev/pipeline.git` <br>
Run the plugin on the standalone instance <br>
>> cd pipeline <br>
>> atlas-run <br>
You may notice a long initial load up time.  After it is successfully launched, navigate to 
localhost:6990/bamboo on your browser. <br>
You will see the plugin in the top menu bar labeled as "Pipeline". </li>
<li> [Optional] If you want to work in Eclipse, follow these instructions: <br>
- <a href="https://developer.atlassian.com/display/DOCS/Set+Up+the+Eclipse+IDE+for+Windows"> Set up the Eclipse IDE </a> </li>
<li> [Optional] You may also want to set up your app link to JIRA, follow our <a href="#installation-instructions"> installation instruction </a> </li>
<li>You're all set!</li>
</ol>
If you have any questions, feel free <a href="mailto:pipelineplugin@cobalt.com"> email us </a>!