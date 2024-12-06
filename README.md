# Adobe Coding Challenge

## Problem
Take a variable number of identically structured json records and de-duplicate the set.

An example file of records is given in the accompanying 'leads.json'.  Output should be same format, with dups reconciled according to the following rules:

1. The data from the newest date should be preferred.

2. Duplicate IDs count as dups. Duplicate emails count as dups. Both must be unique in our dataset. Duplicate values elsewhere do not count as dups.

3. If the dates are identical the data from the record provided last in the list should be preferred.

Simplifying assumption: the program can do everything in memory (don't worry about large files).

The application should also provide a log of changes including some representation of the source record, the output record and the individual field changes (value from and value to) for each field.

> NOTE: `leads.json` is provided as an example for the problem.