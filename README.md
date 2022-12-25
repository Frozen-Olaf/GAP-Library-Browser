# GAP-Library-Browser
## version: v0.2.0 update:
  * Now for searching methods through arguments, the browser searches by ***GAP filters***, instead of GAP categories from previous versions. This is under the consideration of including certain unique properties of some methods.
  * Added web-style tabs and traverse buttons so that user can easily traverse across different search result pages.
  * Added a secondary search bar on search result page.
  * Added a filtering option in search result page that can filter out "trivial" methods for display. Trivial methods are: 
    - system getters and setters
    - system mutable setters
    - default methods that do nothing, from method installation
  * Updated some GUI component and fixed some bugs.
  
------------------------------------------------------------------------------------------------------------------------------
### This project is a tool to browse the libraries and packages of the GAP system.
### It consists of two parts: 
###  1. Dumping of GAP (written in GAP language)
###  2. The Browser Software with GUI (written in Java)

------------------------------------------------------------------------------------------------------------------------------
## Dumping of GAP:

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

------------------------------------------------------------------------------------------------------------------------------
## The Browser Software with GUI:

This part of the project is the browser itself.
It is managed and wrapped by [Apache Maven](https://maven.apache.org/index.html).
It can read the dumped JSON file, and allow user to perform searches within, and display the results in a table. 

To compile and run the browser, simply in command line:
  1. cd to the directory of this project
  2. run the following command:
> java -jar target/GAP_Library_Browser_v0.1.jar
        
#### Note:
This project CANNOT run with Java JDK < 8 (not incl. 8), 

and it has not been tested on JDK < 11 yet.
  
### Search:
The searches that can be performed in the browser generally consist of the following three types:

####  1. search a GAP operation by name, returns all the methods under that operation.
####  2. search a number of GAP filters, returns the methods whose arguments ***altogether*** are under the specified filters.
####  3. search GAP methods by a combination of method name and specified filters of arguments applicable to that method.
  
#### Note:
By "***altogether***" in type 2, I mean the union set of all filters of all the arguments of a method.

### Search Method (Type 3)
Now, more specifically on type 3, the search input should be of the following formats:

If you only want to search for methods under certain filters and by certain method name pattern:
> method_name(filter1, filter2, filter3, ...)

If you would like to impose an order on the 


ories of arguments of the method for searching:
> method_name([arg1_filter1, ...], [arg2_filter1, arg2_filter2, arg2_filter3], ...)

#### Note:
Everthing in the search input should be separated by a comma ', '. 
Whitespaces generally don't matter.

When specifying the order of the filters of arguments of a method, as shown above, the content between the delimiters '[' and ']' represent filters for a argument of the method.


### Subset Symbol '...'
As you may be wondering what the role of '...' is:

If user enters '...' as (the ending) part of their input, then it specifies that the search results should be a superset of the user input. This is only applicable when user is searching filters or methods.

For example: 
When searching the filters: 
> IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne 

it will return all the methods whose arguments **altogether** belong to exactly these filters,
whereas when searching:
> IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, ...

it will return all the methods whose arguments **altogether** at least belong to these filters.
    
For searching method (type 3):
  1. This allows user to enter '...' at the end of input, separated priorly by a comma, to search for methods which take more arguments than the currently specified argument number, and based on the current imposed argument order.

For instance, when searching
> method_name([arg1_filter1, arg1_filter2], [arg2_filter1, arg2_filter2, arg2_filter3], ...)

it will return all the methods that match the name pattern, at least take two arguments, and at the same time, the first two arguments are of the filters exactly as specified by the user. Methods returned can take more than two arguments as long as the condition is met.
   
  2. This allows user to specify that, for each argument of the method, whether they want to permit searching for methods that have a superset of filters at that exact argument position.

For instance, when searching
> method_name([arg1_filter1, ...], [arg2_filter1, ...])

it will return all the methods that match the name pattern, take exactly two arguments, and at the same time, each of these two arguments is of a superset of the filters as specified by the user at that argument position.
   
  3. This also allows user to bypass arguments at certain positions of the method.

For instance, to specify only the filters of the second argument of the method to search, and ignore the first argument and the third argument, one can do
> method_name([...], [arg2_filter1, arg2_filter2, arg2_filter3, ...], [...], ...)

it will return all the methods that match the name pattern, take at least three arguments, and at the same time, the second argument is of a superset of the filters as specified by the user.


------------------------------------------------------------------------------------------------------------------------------
#### Warning:
The following functionality is ***deprecated*** in version 0.2.0 and all upcoming versions, due to the switch from searching by only GAP categories to now by GAP filters.

##### Empty Argument Symbol: \void
To search for methods that take no arguments, there are two ways:
  1. When searching filter, enter as in the search input:
> \void
  2. When seatching method, enter
> method_name(\void)


## Hope you have fun with this browser! :)
     
