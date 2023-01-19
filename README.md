# GAP-Library-Browser
## version: v1.0.0

------------------------------------------------------------------------------------------------------------------------------
### This project is a tool to browse the libraries and packages of the GAP system.
### It consists of two parts: 
    1. Dumping of GAP (written in GAP language)
    2. The Browser Software with GUI (written in Java)

### The following page has more information on what the browser can do:
* [Features](https://github.com/Frozen-Olaf/GAP-Library-Browser/wiki#welcome-to-the-gap-library-browser-wiki)
* [Search](https://github.com/Frozen-Olaf/GAP-Library-Browser/wiki#welcome-to-the-gap-library-browser-wiki)

### Note:
The browser runs OK on linux or unix-based OS, however, on Windows, some functionality such as the display of a source code file path is compromised.

------------------------------------------------------------------------------------
# Dumping of GAP:

This part of the project is implemented by a script 'dump.g' that dumps all the methods under all the operations currently availlable in GAP into a JSON file, covering information such as:
  * name of the method;
  * filters to which arguments of the method are applicable;
  * rank of the method;
  * file path to the source code file in which the method is implemented;
  * line number range in the source code file that includes the implementation of the method.

To run the dumping, a running GAP session with **package io** and **package json** loaded is required.
If your GAP hasn't already have the two packages, they can be downloaded here:
  * for pkg io: https://gap-packages.github.io/io/
  * for pkg json: https://gap-packages.github.io/json/
  
To load a pkg into a running GAP session, one can use the command:
> LoadPackage("pkg_name");

After you make sure the two required packages are loaded onto your GAP session,
to run the dumping, use the command: 
> Read("file_path_to_dump.g");

If nothing goes wrong :), then a JSON file under the name format 'dump-current_datetime.json' will be created, which contains all the dumped information!

#### Note:
Dumping so far has only been tested on GAP 4.12.0 & 4.12.1.

------------------------------------------------------------------------------------
# The Browser Software with GUI:

This part of the project is the browser itself.
It is managed and wrapped by [Apache Maven](https://maven.apache.org/index.html).
It can read the dumped JSON file, and allow user to perform searches within, and display the results in a table. 

To compile and run the browser, simply in command line:
  1. cd to the directory of this project
  2. run the following command (specify the optional argument 'dark' if you would like to start the browser in dark mode):
> java -jar target/GAP_Library_Browser_v1.0.0.jar [dark]
        
### Note:
This project CANNOT run with Java JDK < 8 (noninclusive), and it has not been tested on JDK < 11 yet.
***

# Hope you have fun with this browser! :)

## This project acknowledges the following icons:
* <a href="https://www.flaticon.com/free-icons/home-button" title="home button icons">Home button icons created by bqlqn - Flaticon</a>
* <a href="https://www.flaticon.com/free-icons/back" title="back icons">Back icons created by Handicon - Flaticon</a>
* <a href="https://www.flaticon.com/free-icons/arrow" title="arrow icons">Arrow icons created by Handicon - Flaticon</a>
* <a href="https://www.flaticon.com/free-icons/typing" title="typing icons">Typing icons created by Prashanth Rapolu 15 - Flaticon</a>
* <a href="https://www.flaticon.com/free-icons/clear" title="clear icons">Clear icons created by The Icon Tree - Flaticon</a>
* <a href="https://www.flaticon.com/free-icons/search-bar" title="search bar icons">Search bar icons created by Tanah Basah - Flaticon</a>
* <a href="https://www.flaticon.com/free-icons/delete" title="delete icons">Delete icons created by Kiranshastry - Flaticon</a>
* <a href="https://www.flaticon.com/free-icons/filter" title="filter icons">Filter icons created by joalfa - Flaticon</a>
* <a href="https://www.flaticon.com/free-icons/sun" title="sun icons">Sun icons created by Freepik - Flaticon</a>
* <a href="https://www.flaticon.com/free-icons/moon" title="moon icons">Moon icons created by Freepik - Flaticon</a>
