# Study_Manager
Author/ Mohammed Alghamdi<br />
Framework: Android Studio is used with Java 8 as a front-end language, so it is native app.<br />
Libraries: 
1- Room Resistance is utilized to help writing the code for the RDBS.<br />
2- CalendarView material Deisgn is used for remarks' calendar.
https://github.com/prolificinteractive/material-calendarview/blob/master/LICENSE
<br />
Application's link at Google Play:  https://play.google.com/store/apps/details?id=creative.developer.m.studymanager

# Genreal notes: 
1- Editing is not allowed for this repository because it is a personal project.<br />
2- The project uses MVVM software archaticture.<br />
3- This repository represents the first update's version.

# Model files 
Model classes are on dpFile, which includes class that is related directly to managing the database or managing the 
retirieved data as object, and EntityListFiles, which includes classes that have the data structures to manage the data 
that is retreived from the data base.

dbFile's classes: <br />
DataRepository/ It eases and facilitates the interaction with the data base.<br />
AppDatabse/  It is used to generate a new data base or find an existing one.<br />
AssignmentsEntity/ It is used to represent a signle assignment as an object and as a field on a database table
called AssignmentEntity.<br />
CourseEntity/ It is used to represent a signle course as an object and as a field on a database table
called CourseEntity. <br />
FlashCardEntity/ It is used to represent a signle flash card as an object and as a field on a database table
called FlashCardEntity. <br />
LessonEntity / It is used to represent a signle lesson as an object and as a field on a database table 
called LessonEntity <br />.
NoteEntity/ It is used to represent a signle notes' class as an object and as a field on a database table
called NoteEntity. <br />
RemarkEntity/ It is used to represent a signle remark as an object and as a field on a database table 
called RemarkEntity. <br />
dbFile's interfaces: 
AssignmentDao/ It stores methods that will be used as procedures for AssignmemtEntity table.<br />
CourseDao/ It stores methods that will be used as procedures for CourseEntity table. <br />
FlashCardsDao/ It stores methods that will be used as procedures for FlashCardEntity table.<br />
LessonDao/ It stores methods that will be used as procedures for LessonEntity table. <br />
NoteDao/ It stores methods that will be used as procedures for NoteEntity table. <br />
RemarkDao/ It stores methods that will be used as procedures for RemarkEntit table. <br />

EntityListFiles:<br />
AssignmentsList/ It handles storing assignemnts objects in a data structure and manipulate them.<br />
FlashCardsList/ It handles storing cards objects and manipulate them.<br />
NoteList/ It handles storing notes objects as a data structure and manipulate them.<br />
RemarksList/ It handles storing remarks objects in a data structure and manipulate them.<br />


# View files (.xml files at layout folder) with one class
AlarmManagement/ It manages creating and showing a notification for the user.

# ModelView files
classes: <br />
AddAssignmentActivity/ It is responsible for adding an assignment or editing
an existing one.<br />
AddFlashCardActivity/ It is responsible for adding a flash card or editing
an existing one. <br />
AddNoteActivity/ It is responsible for adding a flash card or editing an existing one.<br />
AddRemarkActivity/ It is responsible for adding an remark or editing an existing one. <br />
AssignmentActivity/ It is responsible for assignment activity interaction with the user. <br />
CoursesActivity/ It is responsible for courses activity interaction with the user. <br />
FlashCardsActivity/ It is responsible for flash cards activity interaction with the user. <br />
HomeActivity/ It is responsible for home activity interaction with the user. <br />
NotesActivity/ It is responsible for notes activity interaction with the user. <br />
RemarksActivity/ It is responsible for remark activity interaction with the user. <br />

# Features to be added ():
update 1: <br />
improve assignemts view. <br />
calender view for remarks. <br />
show bigger picture for the schedule. <br />
organize how to select a lesson. <br />
fragment layout to improve browsing. <br />
notification for assignments. <br />

update 2:
making ModelManager for each section, so that model view intract with one object instead of two. <br />
study reminders . <br />

update 3: 
adding pictures to lessons notess. <br />
animation of fliping a card. <br />
adding sounds and visual effects. <br />

update 4:
import + share notes and flash cards <br />
user input all courses at once. <br />

Later on:
ability to change lesson and a course <br />
Making courseActivity into two files. FlashCardActivity courses can be edited from coursesActivity. <br />
Selecting a course from drop-down list on adding/editing rather than typing it. <br />
make the phone silent during the classes. <br />


credits:
Sounds's Crdits:
1- https://freesound.org/people/JFRecords/sounds/420504/
2- https://freesound.org/people/hykenfreak/sounds/202029/


