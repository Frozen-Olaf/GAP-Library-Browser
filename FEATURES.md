# Welcome to the GAP-Library-Browser wiki!
## Here I will explain some 'hidden' functionalities of the GAP Library Browser, and how to use them :).
## These functionalities are:
  * Search input suggestions
    - Search histories
  * Open a file from the browser
  * Runtime light/dark theme switch
  * Sorting the table from a search result
  * Filter the table from a search result to focus on certain content
    - Hide trivial GAP methods
  * View full text for (very) long text content
  * Search histories

***
### Search input suggestions
This browser supports search suggestions based on your input so that you don't need to type in long names from GAP! The search input suggestions list is scrollable, and the first five items (at most) on the list are from your search histories, if there was any.

You can use either keyboard or mouse cursor to navigate through the suggestions and to confirm one suggestion by a mouse click or pressing the `Enter` key. Also, you can press the `Esc` key to close the suggestions!

<img width="800" alt="Screenshot 2023-01-19 at 22 22 03" src="https://user-images.githubusercontent.com/114816107/213575509-806cd872-8722-45db-a6d6-2646f9d99b51.png">

#### Search histories
Search histories are preserved every time a valid search result is returned in a new page. When user is entering input to search, any relevant search history is displayed at the top of the search suggestion list in colour purple, up to 5 history entries.

<img width="800" alt="Screenshot 2023-01-19 at 22 57 10" src="https://user-images.githubusercontent.com/114816107/213580742-e9621e29-adb3-4ea6-b6bf-e20241bb83d5.png">

***
### Open a file from the browser
<img width="912" alt="Screenshot 2023-01-19 at 22 23 56" src="https://user-images.githubusercontent.com/114816107/213575828-5ff0867a-6351-4b2f-ac61-dfefe5c7fd3e.png">

Here in the search result page for searching the GAP operation `Order`, in the table column `File Path`, the path of the file in which the GAP method is installed to GAP is displayed.

* Double-click the content, the browser will open a new page that displays the file with the corresponding code block highlighted for the method.
* While pressing `Shift`, mouse-clicking the content, the browser will open the directory where the file resides in your file system, with the file being highlighted.
* While pressing `Ctrl`, mouse-clicking the content, the browser will open the file directly.

#### Note:
For opening the file directly, if in your OS the text editor is not previously specified for GAP file with extensions `.g`, `.gi` or `.gd`, then the following error message will show up:

<img width="848" alt="Screenshot 2022-12-26 at 12 27 28" src="https://user-images.githubusercontent.com/114816107/209548991-4c654dfc-4548-4105-ba72-e57f9d4cf5b4.png">

To solve this issue, one option is to open the directory of the file by clicking the file path while pressing `Shift`, then from there open the file manually and specify the default text editor for it. Now if you use the browser to open the same kind of files, the browser will open the file via the text editor that you specified!

#### Note:
In the table, many file paths are of canonical form: `./path/to/file`. In such cases, `.` represents the root path of the GAP system in your computer. For file paths not of this form, it means that the files behind these paths reside elsewhere from the GAP system, however, this will not affect opening the file from the browser. The GAP root path can be checked in a running GAP session via the command: 

`GAPInfo.RootPaths;` the last element of the output is the GAP root path.

or simply, 
`GAPInfo.RootPaths[3];`

***
### Runtime light/dark theme switch
You can switch to dark or light theme anytime you want! Below are some examples for comparison.

Light Theme                |  Dark Theme
:-------------------------:|:-------------------------:
<img width="600" alt="Screenshot 2023-01-19 at 22 36 36" src="https://user-images.githubusercontent.com/114816107/213577984-3c13ccc4-f46b-40e4-beba-0522e927fd5f.png">  |  <img width="600" alt="Screenshot 2023-01-19 at 22 42 40" src="https://user-images.githubusercontent.com/114816107/213578851-173fa46a-36cc-42d3-87d5-4b99a8fb75f7.png">
<img width="600" alt="Screenshot 2023-01-19 at 22 43 36" src="https://user-images.githubusercontent.com/114816107/213578935-6b3eac68-81a1-4c0c-9056-8dc457b71cd3.png">  |  <img width="600" alt="Screenshot 2023-01-19 at 22 44 00" src="https://user-images.githubusercontent.com/114816107/213578932-54182f29-cf6a-4a7d-90e0-498777cdc7d6.png">
<img width="600" alt="Screenshot 2023-01-19 at 22 37 10" src="https://user-images.githubusercontent.com/114816107/213578269-69276ab7-54eb-413a-b5fd-7a30a61d0fcb.png">  |  <img width="600" alt="Screenshot 2023-01-19 at 22 37 40" src="https://user-images.githubusercontent.com/114816107/213578964-8f836d55-f8f4-4238-a4c7-56b58dee2dd0.png">

***
### Sorting the table
You can sort the table by clicking column header, the upper or lower triangle on the column header represents if the table is in ascending or descending natural order respectively.

***
### Filter the table
You can filter the table by entering text into the box on top left to filter out all the non-matching methods in the table, retaining only the matching ones. This filtering is applied to all the content of the table, in other words, you can filter by `method name`, `argument filter`, `rank value`, `source code file path`, and the `line number range` in the source code file.

You can click the 'Hide Trivials' checkbox to filter out all less interesting methods:
  - system getters/setters
  - system mutable setters
  - default methods that do nothing, derived automatically from method installation.

Below is an example of filtering the table derived from searching the GAP operation `Order` to get entries containing the word 'matrix'.
<img width="912" alt="Screenshot 2023-01-19 at 22 27 07" src="https://user-images.githubusercontent.com/114816107/213576283-2891249c-4998-4f74-82d6-9c34be806d6f.png">

***
### View full text for very long text content
For very long texts, view them using the resizable and scrollable full text displayer at the bottom of any search result page. Simply click on an entry of your interest in the table, the full content of it will be displayed at the bottom!

Note: The full text displayer adapts to the size of the window of the browser. So in any case of resizing the window, the displayer will adjust the text displayed so that its area is filled.

<img width="912" alt="Screenshot 2023-01-19 at 22 30 13" src="https://user-images.githubusercontent.com/114816107/213576754-e437ed73-84a6-4560-bdf0-1f35f3160fb2.png">

An alternative way for viewing less longer content, is to hover your cursor over the entry of your interest in the table for a 'tip' full text 
to be displayed. However, note that this is not ideal for a very long text, as the tip could go beyond your screen display!

<img width="800" alt="Screenshot 2023-01-19 at 22 31 16" src="https://user-images.githubusercontent.com/114816107/213576963-f1b083f2-fdd7-4994-afd8-ece917a13766.png">

\
Another potentially helpful way is to expand a column in a table by dragging your cursor on the boundaries of the column header!

<img width="912" alt="Screenshot 2023-01-19 at 22 32 31" src="https://user-images.githubusercontent.com/114816107/213577385-bae16831-661a-4a4e-a0cd-b5cb81ecbddc.png">
