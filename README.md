# Appointment Scheduler

<p>The purpose of this application is to be able to make appointments from any timezone.</p>
<div>
  <p>Author: Alexander Padilla</p>
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

<h2>Notes<h2/>

<h4>Tab Notes:</h4>
<p>
    When a user switches to another tab all the database queries are run for that specific tab so that it can populate with fresh data. 
</p>

<h4>Appointment Tables Notes:</h4>
<p>
    The appointment table in the appointments tab and reports tab are both color coded. Red means the appointment already passed, yellow means the appointment is going on at the moment and green means that it's an upcoming appointment. If the appointment selected has already passed the end time then the user will only be able to update it. If the appointment selected is in progress, the user will not be able to update or delete the appointment until after it is finished. If the appointment selected is an upcoming appointment the user will be able to update or delete the appointment.
</p>

<h4>Date-picker in Appointments Tab Notes:</h4>
<p>
    If the user selects a filter for the appointments' table that is not 'All' or 'Today' a date-picker with the date pre-selected to the next day available that is not that exact same day. The user can select any date in the date-picker that is within the time-frame of our timezone up to one year in the future. If the 'Month' or 'Week' filter is chosen and the user selects a date, the application automatically finds the first day of the month or the first day of the work week of the selected date and the last day of the month or the last day of the work week based on the filter.
</p>

<h4>Appointment Form Notes:</h4>
<p>
    The process of checking if times overlap or exist is automated so that alerts are not necessary. The start time combo-box loads with available start times according to the time duration. I have added a business rule that a user cannot schedule an appointment less than one hour before the start time, so that the contact has time to prepare for their next appointment. 
    If there is a day that is fully booked, that date will not be able to selected. Only dates today and after are enabled in the date-picker so that a user cannot make an appointment in the past.
</p>

<h4>Pie Graph Notes:</h4>
<p>
    The pie graph in the reports tab shows slices with the appointments by type. When hovering over a slice a tooltip appears showing the amount of that type and the name.
</p>


<h2>Directions<h2/> 
<p>
  Upon opening the application the user is prompted with a login screen. Once the user is successfully logged in the main GUI is shown.
  The main GUI consists of three different tabs. The first tab is the Appointments tab. This tab shows the user a table of appointments. This table can be filtered and queried. The user can add an appointment at any given time. If the user wishes to update or delete an appointment they must first select a record from the table.
  If a user selects the add button in the appointment tab a window with a form will pop up but if a user clicks the update button the same form will appear except this time the form will be filled out with information. The customers tab works the exact same way as the appointments tab except that if the user deletes a customer the appointments for that customer will be deleted.
  The reports tab shows three reports. If the user wants to see the amount of different appointment types by month, then they can filter the month in a choice-box within the reports tab.
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
