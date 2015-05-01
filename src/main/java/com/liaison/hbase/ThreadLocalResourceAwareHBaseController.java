package com.liaison.hbase;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.paralleluniverse.strands.concurrent.Semaphore;

import com.liaison.hbase.exception.HBaseControllerLifecycleException;
import com.liaison.hbase.exception.LockAcquisitionException;
import com.liaison.hbase.exception.NonTerminalClosureException;
import com.liaison.hbase.exception.TerminalClosureException;

public abstract class ThreadLocalResourceAwareHBaseController implements Closeable {
    
    private static final int LIFECYCLE_SEMAPHORE_PERMITS = Integer.MAX_VALUE;
    
    private final Logger log;
    private final List<Closeable> allStrandCloseableResources;
    private Semaphore lifecycleSemaphore;
    
    // ||========================================================================================||
    // ||    INSTANCE METHODS: API                                                               ||
    // ||----------------------------------------------------------------------------------------||
    
    /**
     * TODO
     * @param resource
     * @throws HBaseControllerLifecycleException
     */
    protected final void addCloseableResource(final Closeable resource) throws HBaseControllerLifecycleException {
        final String methodName = "addCloseableResource";
        boolean entered = false;
        if (resource != null) {
            try {
                entered = enter(methodName);
                this.allStrandCloseableResources.add(resource);
            } finally {
                if (entered) {
                    leave(methodName);
                }
            }
        }
    }
    
    /**
     * TODO
     * @return
     * @throws HBaseControllerLifecycleException
     */
    protected final int countCloseableResources() throws HBaseControllerLifecycleException {
        final String methodName = "countCloseableResources";
        boolean entered = false;
        try {
            entered = enter(methodName);
            return this.allStrandCloseableResources.size();
        } finally {
            if (entered) {
                leave(methodName);
            }
        }
    }
    
    /**
     * TODO
     * @param methodName
     * @return
     * @throws HBaseControllerLifecycleException
     */
    private final boolean enter(final String methodName) throws HBaseControllerLifecycleException {
        String logMsg;
        try {
            if (!this.lifecycleSemaphore.tryAcquire()) {
                throw new LockAcquisitionException();
            }
        } catch (LockAcquisitionException | NullPointerException exc) {
            logMsg = "Failed to release semaphore at method '"
                     + methodName
                     + "'; vault controller is in an unavailable lifecycle state "
                     + "(e.g. shutdown/destruction; semaphore:"
                     + this.lifecycleSemaphore
                     + ")";
            log.error(logMsg);
            throw new HBaseControllerLifecycleException(logMsg);
        }
        return true;
    }
    
    /**
     * TODO
     */
    private final void leave(final String methodName) throws HBaseControllerLifecycleException {
        String logMethodName = null;
        String logMsg;
        try {
            // >>>>> LOG >>>>>
            if (log.isTraceEnabled()) {
                logMethodName = "leave(" + methodName + ")";
                log.trace("[" + logMethodName + "] releasing permit...");
            }
            // <<<<< log <<<<<
            this.lifecycleSemaphore.release();
            // >>>>> LOG >>>>>
            if (log.isTraceEnabled()) {
                log.trace("[" + logMethodName + "] permit released");
            }
            // <<<<< log <<<<<
        } catch (NullPointerException npExc) {
            logMsg = "Failed to release semaphore at method '"
                     + methodName
                     + "'; vault controller is in an unavailable lifecycle state "
                     + "(e.g. shutdown/destruction; semaphore:"
                     + this.lifecycleSemaphore
                     + ")";
            log.error(logMsg);
            throw new HBaseControllerLifecycleException(logMsg);
        } catch (Throwable overflowError) {
            /*
             * The current implementations of Semaphore in both the Java API
             * (http://grepcode.com/file/repository.grepcode.com/java/root/jdk/openjdk/8-b132/java/util/concurrent/Semaphore.java#187)
             * and Quasar
             * (https://github.com/puniverse/quasar/blob/e2379cbaaa753e9cc6cdc9bafe7701a51755a429/quasar-core/src/main/java/co/paralleluniverse/strands/concurrent/Semaphore.java#L181)
             * specify that:
             * 
             *     (1) Semaphore#release() adds 1 to the total count of permits available
             *     (2) The initial number of permits given in the Semaphore constructor is NOT a
             *         hard limit; i.e. release() increases the available count regardless of
             *         whether or not a permit was acquired in the first place
             *     (3) If there is a detectable integer overflow on the count of available permits
             *         (i.e. the total number of available permits after release() is a smaller int
             *         than prior to release), then an Error (!) is thrown with the message
             *         "Maximum permit count exceeded".
             * 
             * Since the semaphore for this VaultController is specified with an initial permit
             * count of Integer.MAX_VALUE, any release of an unacquired permit will result in this
             * Error. This SHOULD NEVER HAPPEN -- the methods in this class release in a finally
             * block, but only if a flag is set indicating that the permit was acquired initially.
             * 
             * However, to prevent an Error from being thrown in case of some unanticipated failure
             * scenario wherein a release occurs which brings the total available permit count out
             * of integer range, catch Throwable (including any Errors) on Semaphore#release.
             * 
             * In the event of that unanticipated error, the lifecycle semaphore can no longer be
             * trusted to track method entry/exit, as the unexpected release() indicates that
             * something has gone awry in the acquire/release choreography. Therefore, the
             * controller at that point will SHUTDOWN UNGRACEFULLY -- destroying the instance and
             * closing all resources.
             */
            logMsg = "FATAL ERROR during attempt to release lifecycle semaphore at method '"
                     + methodName
                     + "': vault controller is now in an INCONSISTENT STATE and will be terminated"
                     + " ungracefully; semaphore:"
                     + this.lifecycleSemaphore
                     + "); error: "
                     + overflowError;
            log.error(logMsg, overflowError);
            destroyLifecycleSemaphore();
            destroyResources();
        }
    }

    /**
     * 
     * @throws InterruptedException
     * @throws HBaseControllerLifecycleException
     */
    private final void acquireAllPermits() throws InterruptedException, HBaseControllerLifecycleException {
        String logMsg;

        try {
            this.lifecycleSemaphore.acquire(LIFECYCLE_SEMAPHORE_PERMITS);
        } catch (NullPointerException npExc) {
            logMsg = "Failed to acquire all permits; vault controller is in an unavailable "
                      + "lifecycle state (e.g. shutdown/destruction; semaphore:"
                      + this.lifecycleSemaphore
                      + ")";
            log.error(logMsg);
            throw new HBaseControllerLifecycleException(logMsg);
        }
    }
    
    private final void destroyLifecycleSemaphore() {
        this.lifecycleSemaphore = null;
    }
    
    /**
     * Close all HBase resources held by this controller instance (including those bound to
     * ThreadLocals, provided that they have been registered in
     * {@link #allStrandCloseableResources}). This action takes place in 2 phases:
     * <br>
     * <ol>
     * <li>Acquire ALL permits allocated to the lifecycle semamphore in order to ensure that there
     * are no threads currently accessing this controller, and prevent any further threads from
     * entering it. Provided that all public methods call {@link #enter(String)} as their first
     * step, any thread which attempts to invoke a member of this instance's public API after this
     * step is complete will have a {@link VaultControllerLifeCycleException} thrown.</li>
     * <li>Iterate through {@link #allStrandCloseableResources} and close each Closeable resource
     * registered in the list.</li>
     * </ol>
     * <br>
     * It is important to note that the ThreadLocals pointing to the resources closed in phase 2
     * described above will <b>have no way to know</b> that the resources they contained were
     * closed by an outside thread, so <b>all ThreadLocal instances created by this instance -- as
     * well as any values they contain -- must no longer be used after this method runs</b>, as
     * they will be an in inconsistent/undefined state.
     * <br><br>
     * Since this method renders the controller object unusable, after invocation, the application
     * must ensure that all strong or soft references to this object are dropped. There may still
     * be internal Thread references to the data created in this object's ThreadLocal instances,
     * but as those are weak references
     * (see: {@link http://www.docjar.com/docs/api/java/lang/ThreadLocal$ThreadLocalMap.html}) and
     * because weak references do not prevent reclamation/garbage collection, remaining vestiges of
     * this object's ThreadLocal references stored in the Thread will be removed at next garbage
     * collection.
     * <br><br>
     * No ThreadLocals were harmed in the making of this film.
     * <br><br>
     * @return boolean indicating whether all resources were closed; if false, then 1 or more
     * resources threw an Exception during the close operation, and could therefore not be closed
     * @throws HBaseControllerLifecycleException if the thread on which the destroy invocation was
     * made is interrupted before the lifecycle semaphore can be obtained. If this exception is
     * thrown, it is safe to retry destruction of this object.
     */
    private final void destroyResources() throws HBaseControllerLifecycleException {
        final HBaseControllerLifecycleException resourceClosureFaults;

        // Iterate through the collection of closeable resources associated with this object and
        // close each one. If no exceptions are thrown during closure, then return true; otherwise
        // log the exception and return false, but continue attempting to close other resources in
        // the collection.
        resourceClosureFaults =
            this.allStrandCloseableResources
                .stream()
                .reduce((HBaseControllerLifecycleException) null,
                        (HBaseControllerLifecycleException vlcExc, Closeable c) -> {
                            final String logMsg;
                            try {
                                c.close();
                                if (log.isTraceEnabled()) {
                                    log.trace("Resource closed: " + c);
                                }
                                return vlcExc;
                            } catch (IOException ioExc) {
                                logMsg = "Failed to close one or more resources";
                                log.warn("Failed to close resource ("
                                         + c
                                         + "); "
                                         + ioExc.toString(),
                                         ioExc);
                                if (vlcExc == null) {
                                    vlcExc = new HBaseControllerLifecycleException(logMsg);
                                }
                                vlcExc.addInternalResourceException(c, ioExc);
                                return vlcExc;
                            }
                        },
                        (HBaseControllerLifecycleException vlcExc1,
                         HBaseControllerLifecycleException vlcExc2) -> 
                            HBaseControllerLifecycleException.mergeToNew(vlcExc1, vlcExc2));
        if (resourceClosureFaults != null) {
            throw resourceClosureFaults;
        }
    }

    protected void preDestroyResources() {
        // no-op, overrideable
    }
    protected void postDestroyResources() {
        // no-op, overrideable
    }

    @Override
    public final void close() throws IOException {
        final String logMsg;
        try {
            /*
             * Acquire all of the permits available to the lifecycle semaphore; this action should
             * ensure that there are no methods executing, as all public methods start by acquiring
             * the lifecycle semaphore.
             */
            acquireAllPermits();
            /*
             * Render the lifecycle semaphore unusable, terminally disabling this controller.
             */
            destroyLifecycleSemaphore();
            /*
             * Execute any steps the inheriting implementation requires prior to closing registered
             * resources. (No-op in the default implementation.)
             */
            preDestroyResources();
            /*
             * Close registered resources.
             */
            destroyResources();
            /*
             * Execute any steps the inheriting implementation requires after closing registered
             * resources. (No-op in the default implementation.)
             */
            postDestroyResources();
        } catch (InterruptedException iExc) {
            logMsg = "Destruction thread interrupted before lifecycle semaphore obtained; "
                     + "controller destruction was not completed; "
                     + iExc.toString();
            log.warn(logMsg, iExc);
            throw new NonTerminalClosureException(logMsg, iExc);
        } catch (HBaseControllerLifecycleException vclExc) {
            logMsg = "Failed to close " + getClass().getSimpleName() + "; " + vclExc.toString();
            log.warn(logMsg, vclExc);
            throw new TerminalClosureException(logMsg, vclExc);
        }
    }
    
    public ThreadLocalResourceAwareHBaseController() {
        this.log = LoggerFactory.getLogger(getClass());
        this.lifecycleSemaphore = new Semaphore(LIFECYCLE_SEMAPHORE_PERMITS);
        this.allStrandCloseableResources = Collections.synchronizedList(new LinkedList<>());
    }

}
