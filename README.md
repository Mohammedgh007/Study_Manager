# Study_Manager
Author/ Mohammed Alghamdi<br />
Framework: Android Studio is used with Java 8 as a front-end language, so it is native app.<br />
Libraries: Room Resistance is utilized to help writing the code for the RDBS.<br />
Application's link at Google Play:  https://cutt.ly/MwHJypX

# Genreal notes: 
1- Editing is not allowed for this repository because it is a personal project.<br />
2- The project uses MVVM software archaticture.<br />

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


# View files (.xml files at layout folder)

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

# Features to be added (the list is not Not ordered):
- improve assignemts and remarks view. <br />
- abstract/interface to capture redundancy on EntityList. <br />
- ability to change lesson and a course. <br />
- animation of fliping a card. <br />
- adding sounds and visual effects. <br />
- adding calender view for Remarks. <br />
- adding pictures to lessons notess. <br />
- Create SortedList class to store list of AssignmentEntity. <br />
- cut the intended part of courses' photo. <br />
- Making courseActivity into two files. FlashCardActivity courses can be edited from coursesActivity. <br />
- making AssignmentObj class that handles the database as well. <br />
- Create a set of class that control communicate with repository and EntityLists. <br />
- Selecting a course from drop-down list on adding/editing rather than typing it.<br />
- Adding notification feature for reminding the assignments. <br />


