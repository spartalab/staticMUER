# staticMUER
This repository contains code for solving multiclass user equilibrium with recourse using link-based methods. 

The current implementation uses method of successive averages (MSA) for obtaining convergence and assumes discrete value of time distribution.
The future versions will extend the analysis for continuous VOT distributions and other link-based methods.

The inputs are specified inside a folder under 'Networks/' including _Links.txt_ (information about links and various link-states), _Trips.txt_ (demand from origin node to a destination node), and _VOT.txt_ (discrete VOT distribution).
The output is the flow under each link state for each link (_LinkStateFlows.txt_), variation of relative gap with iterations (_GapRate.txt_), and an aggregated average flow on each link (_TotalLinkStateFlows.txt_). The output is written inside the respective 'Networks/' folder

Created by Venktesh Pandey <venktesh@utexas.edu>. If you have questions, feel free to email.
