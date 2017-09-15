# Adaptive Media Peformance

## Setup
1. Install jmeter:
    * For mac: `brew install jmeter`
    * Or download from [here](https://jmeter.apache.org/download_jmeter.cgi).

2. Start a fresh liferay instance.

3. Setup the data for the performance tests by deploying the module:

        gradle deploy

    This will create:
    * A site with the blogs portlet (http://localhost:8080/web/adaptive-media-performance-testing).
    * A blogs entry with 50 adaptable images.
    * The adaptive media configurations required in [LPS-71513](https://issues.liferay.com/browse/LPS-71513).

## Tests
### Impact on image visualization on blogs

1. Test with adaptive media:

        jmeter -n -t src/test/jmeter/performance.jmx

2. Disable adaptive media for blogs:
    1. Open the Gogo shell.
    2. Stop the module `Liferay Adaptive Media Blogs Web`.

3. Run the test again (step 1).

4. Compare the final results of each run:

        summary =   1000 in 00:00:22 =   45.1/s Avg:    21 Min:    18 Max:    72 Err:     0 (0.00%)

    Pay special attention to the first number (requests per second).

**Note:** The script (`src/test/jmeter/performance.jmx`) is configured to hit `http://localhost:8080`. This (and everything else) can be changed either manually modifying the jmx file or via jmeter GUI.


### Impact of upload of images with / without Adaptive Media

TODO

### Impact of image optimization process

TODO
