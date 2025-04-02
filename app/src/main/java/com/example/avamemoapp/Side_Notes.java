/*
DatePickerDialog:
    - A dialog that displays a calendar view for selecting a date.
    - It allows users to pick a date from a calendar interface.
    - The selected date is returned to the calling activity or fragment through a listener.

MainActivity:
    - The main entry point of the application.
    - It typically contains the main user interface and handles user interactions.
    - It can start other activities or fragments to display different screens or functionalities.

Memo.java:
    - A model class representing a memo.
    - It contains properties like title, text, date, and priority.
    - It may include methods for data manipulation and formatting.

MemoListActivity:
    - An activity that displays a list of memos.
    - It may include features like searching, filtering, and sorting memos.
    - It can also handle user interactions like adding, editing, or deleting memos.

MemoSettingsActivity:
    - An activity that allows users to configure settings related to memos.
    - It may include options for sorting, filtering, and displaying memos.
    - It can also handle user preferences and save them for future use.

 MemoAdapter:
    - An adapter class that binds memo data to a RecyclerView.
    - It handles the layout and display of each memo item.
    - It may include features like click listeners for user interactions.

MemoDataSource:
    - A data source class that manages the storage and retrieval of memo data.
    - It may handle database operations, file I/O, or in-memory storage.
    - It provides methods for adding, updating, deleting, and querying memos.

MemoDbHelper:
    - A database helper class that manages the SQLite database for memo storage.
    - It handles database creation, version management, and CRUD operations.
    - It provides methods for executing SQL queries and managing database connections.

string.xml:
    - A resource file that contains string resources used in the application.
    - It allows for localization and easy management of text strings.
    - It may include app titles, button labels, error messages, and other UI text.

memo_item.xml:
    - A layout file that defines the UI for a single memo item in the RecyclerView. (populates into the RecyclerView on the MemoListActivity)
    - It includes views like TextViews, ImageButtons, and other UI elements.
    - It is inflated by the MemoAdapter to display each memo in the list.


 */