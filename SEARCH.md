# Search:
## The search that can be performed in the browser generally consists of the following three types:
###  1. search a GAP operation by name, returns all the methods under that operation.
###  2. search a number of GAP filters, returns the methods whose arguments ***altogether*** are under the specified filters.
###  3. search GAP methods by a combination of method name and specified filters of arguments applicable to that method.
  
#### Note:
By `altogether` in type 2, I mean the union set of all the filters of each argument of a method.

***

### Searching Method (type 3)

#### Graphical User Interface:
In the top main search bar, type in the `method_name` or `~method_name` that you would like to search (`~` indicates that the results should be of the name containing the input pattern, not starting with it. See more down below in the standard search input format section). You can then specify the filters applicable to a particular argument of the methods at the corresponding location. Tick the `Superset` check box if at this particular argument you are searching for methods that have a superset of applicable filters to your input.

#### Note: The search input should not have any empty column at an argument between the other arguments you are searching for. For example, if you would like to search for methods that have a certain set of filters for argument 1, 3 and 5, it is expected that the columns for argument 2, 4 are NOT left empty, and should have `...` in the input or tick the `Superset` check box. However, column for argument 6 can be left empty since it is completely outside the scope of search interest.

<img width="912" alt="Screenshot 2023-01-19 at 21 26 21" src="https://user-images.githubusercontent.com/114816107/213565626-1049249b-b9b4-4deb-9bcc-12ced52ceb70.png">

#### Standard Input Format
This is for the case where you only want to use the top main search bar to enter all the input, or when you are performing a search elsewhere than the home page.

#### If you only want to search for methods under certain filters and by certain method name pattern:
> method_name{filter1, filter2, filter3, ...}

#### If you would like to impose an order on the filters of the arguments of the method for searching:
> method_name{[arg1_filter1, ...], [arg2_filter1, arg2_filter2, arg2_filter3], ...}

#### If you would like to search for methods that contain the input `method_name`, put a `~` symbol at the front of your search input:
> ~method_name{filter1, filter2, filter3, ...}

#### If you would like to search for methods of names starting with the input `method_name`, simply type in:
> method_name{filter1, filter2, filter3, ...}

#### Note:
* Everything in the search input should be separated by a comma `, `, if required.  Whitespaces generally don't matter.
* When specifying the order of the filters of arguments of a method, as shown above, the content between the delimiters `[` and `]` represent filters for an argument of the method.

***

### Subset Symbol `...`
As you may be wondering what the role of `...` is: If user enters `...` as (the ending) part of their input, then it specifies that the search results should be a superset of the user input. This is only applicable when user is searching filters or methods.

For example: 
When searching the filters: 
> IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne 

it will return all the methods whose arguments **altogether** belong to exactly these filters,
whereas when searching:
> IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, ...

it will return all the methods whose arguments **altogether** at least belong to these filters.
    
#### For searching method (type 3):
  1. This allows user to enter `...` at the end of input, separated priorly by a comma, to search for methods which take more arguments than the currently specified argument number, and based on the current imposed argument order.

For instance, when searching
> method_name{[arg1_filter1, arg1_filter2], [arg2_filter1, arg2_filter2, arg2_filter3], ...}

it will return all the methods that match the name pattern, at least take two arguments, and at the same time, the first two arguments are of the filters exactly as specified by the user. Methods returned can take more than two arguments as long as the condition is met.
   
  2. This allows user to specify that, for each argument of the method, whether they want to permit searching for methods that have a superset of filters for that exact argument.

For instance, when searching
> method_name{[arg1_filter1, ...], [arg2_filter1, ...]}

it will return all the methods that match the name pattern, take exactly two arguments, and at the same time, each of these two arguments is of a superset of the filters as specified by the user at that argument position.
   
  3. This also allows user to bypass arguments at certain positions of the method.

For instance, to specify only the filters of the second argument of the method to search, and ignore the first argument and the third argument, one can do
> method_name{[...], [arg2_filter1, arg2_filter2, arg2_filter3, ...], [...], ...}

it will return all the methods that match the name pattern, take at least three arguments, and at the same time, the second argument is of a superset of the filters as specified by the user.

  4. This as well allows user to ignore method name for searching by entering `...` as the method name.

For instance, to specify only the filters of the method to search, and ignore the method name, one can do
> ...{filter1, filter2, filter3, ...}

This is similar to searching by a number of filters, but in this case of searching method, the user can specify the requirement for the arguments in more details (e.g., argument number, argument order, etc.)! Take the following as an example:
> ...{[arg1_filter1, ...], [arg2_filter1, ...], ...}

***

### Empty Argument Symbol: \void
To search for methods that take no arguments, there are two ways:
  1. When searching filter, enter as in the search input:
> \void
  2. When searching method, with `~` optional, enter
> [~]method_name{\void}
