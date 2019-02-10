# staticMUER
This repository contains code for solving multiclass user equilibrium with recourse using link-based methods. 

The current implementation uses method of successive averages (MSA) for obtaining convergence and assumes discrete value of time distribution.
The future versions will extend the analysis for continuous VOT distributions and other link-based methods.

The inputs are specified inside a folder under 'Networks/' including Links.txt (information about links and various link-states),
The output is the flow under each link state for each link and an aggregated average flow on each link.

Created by Venktesh Pandey <venktesh@utexas.edu>. If you have questions, feel free to email.
