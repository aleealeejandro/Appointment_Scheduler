# Appointment Scheduler

<p>The purpose of this application is to be able to make appointments from any timezone.</p>
<div>
  <p>Author: Alexander Padilla</p>
  <p>Contact Information: apadi54@wgu.edu</p>
  <p>Student Application Version: 1.0.0</p>
  <p>Date: 07/05/2022</p>
</div>

<div>
  <p>The application has built in logic to do the following</p>
  <ul>
    <li>check if appointments overlap or exist already so that the user cannot add, update or even see an option for the appointment slot.</li>
    <li>check if the appointment is within the 8am-10pm Mon-Fri EST range so that the user cannot add, update or even see an option for the appointment slot.
    </li>
    <li>check if the appointment goes over the closing time so that the user cannot add, update or even see an option for the appointment slot.
    </li>
    <li>find dates that are fully booked within a year
    </li>
    <li>filter appointment by time ranges
    </li>
    <li>appointment search filters that make the search field dynamic
    </li>
    <li>filter customers by country and division
    </li>
    <li>customer search filters that make the search field dynamic
    </li>
    <li>filter appointment types by month for a pie graph
    </li>
  </ul>
</div>
  
<h2>Directions<h2/> 
<p>
  Upon opening the application the user is prompted with a login screen. Once the user is successfully logged in the main GUI is shown.
  The main GUI consists of three different tabs. The first tab is the Appointments tab. This tab shows the user a table of appointments. This table can be filtered and queried. The user can add an appointment at any given time. If the user wishes to update or delete an appointment they must first select a record from the table.
  If a user selects the add button in the appointment tab a window with a form will pop up but if a user clicks the update button the same form will appear except this time the form will be filled out with information. The customers tab works the exact same way as the appointments tab except that if the user deletes a customer the appointments for that customer will be deleted.
  The reports tab shows two reports. If the user wants to see the amount of different appointment types by month, then they can filter the month in a choice-box.
  If the user wants to log out they can do so at any time by either pressing the logout button or exiting the main GUI.
</p>

<h2>Additional Report Description<h2/>
<p>
  I chose to do a report that checks if there are fully booked dates within a year. If there are fully booked dates, they are shown in a list in the reports tab. 
  This is helpful to the user so that they can quickly check which days are fully scheduled.
</p>

IDE version number: IntelliJ IDEA 2022.1.2 (Community Edition)  
JDK version number: Java SE 17.0.1
JavaFX version compatible with JDK version: JavaFX-SDK-18.0.1
MySQL Connector driver version number: mysql-connector-java-8.0.25

<h2>Scenario</h2>
<p>
You are working for a software company that has been contracted to develop a GUI-based scheduling desktop application. The contract is with a global consulting organization that conducts business in multiple languages and has main offices in Phoenix, Arizona; White Plains, New York; Montreal, Canada; and London, England. The consulting organization has provided a MySQL database that the application must pull data from. The database is used for other systems, so its structure cannot be modified.

The organization outlined specific business requirements that must be met as part of the application. From these requirements, a system analyst at your company created solution statements for you to implement in developing the application. These statements are listed in the requirements section.

Your company acquires Country and First-Level-Division data from a third party that is updated once per year. These tables are populated with read-only data. Please use the attachment “Locale Codes for Region and Language” to review division data. Your company also supplies a list of contacts, which are populated in the Contacts table; however, administrative functions such as adding users are beyond the scope of the application and done by your company’s IT support staff. Your application should be organized logically using one or more design patterns and generously commented using Javadoc so your code can be read and maintained by other programmers.
Requirements

Your submission must be your original work. No more than a combined total of 30% of the submission and no more than a 10% match to any one individual source can be directly quoted or closely paraphrased from sources, even if cited correctly. The originality report that is provided when you submit your task can be used as a guide.


You must use the rubric to direct the creation of your submission because it provides detailed criteria that will be used to evaluate your work. Each requirement below may be evaluated by more than one rubric aspect. The rubric aspect titles may contain hyperlinks to relevant portions of the course.


Tasks may not be submitted as cloud links, such as links to Google Docs, Google Slides, OneDrive, etc., unless specified in the task requirements. All other submissions must be file types that are uploaded and submitted as attachments (e.g., .docx, .pdf, .ppt).
</p>

<h4>A.  Create a GUI-based application for the company in the scenario. Regarding your file submission—the use of non-Java API libraries are not allowed except JavaFX SDK and MySQL JDBC Driver. If you are using the NetBeans IDE, the custom library for your JavaFX .jar files in your IDE must be named JavaFX.</h4>


Note: If you are using IntelliJ IDEA, the folder where the JavaFX SDK resides will be used as the library name as shown in the “JavaFX SDK with IntelliJ IDEA webinar.

<ol>

<li>Create a log-in form with the following capabilities:</li>

•  accepts a user ID and password and provides an appropriate error message

•  determines the user’s location (i.e., ZoneId) and displays it in a label on the log-in form

•  displays the log-in form in English or French based on the user’s computer language setting to translate all the text, labels, buttons, and errors on the form

•  automatically translates error control messages into English or French based on the user’s computer language setting


Note: Some operating systems require a reboot when changing the language settings.


<li>Write code that provides the following customer record functionalities:</li>

•  Customer records and appointments can be added, updated, and deleted.

-  When deleting a customer record, all the customer’s appointments must be deleted first, due to foreign key constraints.

•  When adding and updating a customer, text fields are used to collect the following data: customer name, address, postal code, and phone number.

-  Customer IDs are auto-generated, and first-level division (i.e., states, provinces) and country data are collected using separate combo boxes.


Note: The address text field should not include first-level division and country data. Please use the following examples to format addresses:

•  U.S. address: 123 ABC Street, White Plains

•  Canadian address: 123 ABC Street, Newmarket

•  UK address: 123 ABC Street, Greenwich, London


-  When updating a customer, the customer data auto-populates in the form.


•  Country and first-level division data is pre-populated in separate combo boxes or lists in the user interface for the user to choose. The first-level list should be filtered by the user’s selection of a country (e.g., when choosing U.S., filter, so it only shows states).

•  All the original customer information is displayed on the update form.

-  Customer_ID must be disabled.

•  All the fields can be updated except for Customer_ID.

•  Customer data is displayed using a TableView, including first-level division data. A list of all the customers and their information may be viewed in a TableView, and updates of the data can be performed in text fields on the form.

•  When a customer record is deleted, a custom message should display in the user interface.


<li>Add scheduling functionalities to the GUI-based application by doing the following:</li>
</ol>

<h5>a.  Write code that enables the user to add, update, and delete appointments. The code should also include the following functionalities:</h5>

•  A contact name is assigned to an appointment using a drop-down menu or combo box.

•  A custom message is displayed in the user interface with the Appointment_ID and type of appointment canceled.

•  The Appointment_ID is auto-generated and disabled throughout the application.

•  When adding and updating an appointment, record the following data: Appointment_ID, title, description, location, contact, type, start date and time, end date and time, Customer_ID, and User_ID.

•  All the original appointment information is displayed on the update form in local time zone.

•  All the appointment fields can be updated except Appointment_ID, which must be disabled.


<h5>b.  Write code that enables the user to view appointment schedules by month and week using a TableView and allows the user to choose between these two options using tabs or radio buttons for filtering appointments. Please include each of the following requirements as columns:</h5>

•  Appointment_ID

•  Title

•  Description

•  Location

•  Contact

•  Type

•  Start Date and Time

•  End Date and Time

•  Customer_ID

•  User_ID


<h5>c.  Write code that enables the user to adjust appointment times. While the appointment times should be stored in Coordinated Universal Time (UTC), they should be automatically and consistently updated according to the local time zone set on the user’s computer wherever appointments are displayed in the application.</h5>


Note: There are up to three time zones in effect. Coordinated Universal Time (UTC) is used for storing the time in the database, the user’s local time is used for display purposes, and Eastern Standard Time (EST) is used for the company’s office hours. Local time will be checked against EST business hours before they are stored in the database as UTC.


<h5>d.  Write code to implement input validation and logical error checks to prevent each of the following changes when adding or updating information; display a custom message specific for each error check in the user interface:</h5>

•  scheduling an appointment outside business hours defined at 8:00 a.m. to 10:00 p.m. EST, including weekends

•  scheduling overlapping appointments for customers

•  entering an incorrect username and password


<h5>e.  Write code to provide an alert when there is an appointment within 15 minutes of the user’s log-in. A custom message should be displayed in the user interface and include the appointment ID, date, and time. If the user does not have any appointments within 15 minutes of logging in, display a custom message in the user interface indicating there are no upcoming appointments.</h5>


Note: Since evaluation may be testing your application outside of business hours, your alerts must be robust enough to trigger an appointment within 15 minutes of the local time set on the user’s computer, which may or may not be EST.


<h5>f.  Write code that generates accurate information in each of the following reports and will display the reports in the user interface:</h5>


Note: You do not need to save and print the reports to a file or provide a screenshot.


•  the total number of customer appointments by type and month

•  a schedule for each contact in your organization that includes appointment ID, title, type and description, start date and time, end date and time, and customer ID

•  an additional report of your choice that is different from the two other required reports in this prompt and from the user log-in date and time stamp that will be tracked in part C


<h4>B.  Write at least two different lambda expressions to improve your code.</h4>


<h4>C.  Write code that provides the ability to track user activity by recording all user log-in attempts, dates, and time stamps and whether each attempt was successful in a file named login_activity.txt. Append each new record to the existing file, and save to the root folder of the application.</h4>


<h4>D.  Provide descriptive Javadoc comments for at least 70 percent of the classes and their members throughout the code, and create an index.html file of your comments to include with your submission based on Oracle’s guidelines for the Javadoc tool best practices. Your comments should include a justification for each lambda expression in the method where it is used.</h4>


Note: The comments on the lambda need to be located in the comments describing the method where it is located for it to export properly.


<h4>E.  Create a README.txt file that includes the following information:</h4>

•  title and purpose of the application

•  author, contact information, student application version, and date

•  IDE including version number (e.g., IntelliJ Community 2020.01), full JDK of version used (e.g., Java SE 17.0.1), and JavaFX version compatible with JDK version (e.g. JavaFX-SDK-17.0.1)

•  directions for how to run the program

•  a description of the additional report of your choice you ran in part A3f

•  the MySQL Connector driver version number, including the update number (e.g., mysql-connector-java-8.1.23)

<h4>F.  Demonstrate professional communication in the content and presentation of your submission.</h4>
