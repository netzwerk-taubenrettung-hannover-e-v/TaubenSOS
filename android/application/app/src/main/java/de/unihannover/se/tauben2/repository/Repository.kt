package de.unihannover.se.tauben2.repository

import de.unihannover.se.tauben2.AppExecutors

/**
 * Interface between data and view model. Should only be accessed from any view model class.
 *
 * @param database Instance of local SQLite database
 * @param service Instance of service interface with REST API routes
 * @param appExecutors Instance of app executors class for different threads
 *
 */
class Repository(private val appExecutors: AppExecutors = AppExecutors.INSTANCE){}