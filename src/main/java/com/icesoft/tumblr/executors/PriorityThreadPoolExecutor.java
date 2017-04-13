package com.icesoft.tumblr.executors;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.icesoft.tumblr.state.interfaces.IContext;

/**
 * @author canghailan
 * @datetime 2011-12-10 13:57:19
 */
public class PriorityThreadPoolExecutor extends ThreadPoolExecutor {

    public PriorityThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
            long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public PriorityThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
            long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue,
            RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public PriorityThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
            long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue,
            ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public PriorityThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
            long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue,
            ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
        return new ComparableFutureTask<>(runnable, value);
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        return new ComparableFutureTask<>(callable);
    }

    public class ComparableFutureTask<V>
            extends FutureTask<V> implements Comparable<ComparableFutureTask<V>>,Contextful{
        private Object object;

        public ComparableFutureTask(Callable<V> callable) {
            super(callable);
            object = callable;
        }

        public ComparableFutureTask(Runnable runnable, V result) {
            super(runnable, result);
            object = runnable;
        }

		@Override
        public int compareTo(ComparableFutureTask<V> o) {
            if (this == o) {
                return 0;
            }
            if (o == null) {
                return -1; // high priority
            }
            if(o.getContext() == null || this.getContext() == null || o.getContext().getPriority() == null || this.getContext().getPriority() == null)
            {

            	return 0;
            			
            }else{
            	return  this.getContext().getPriority().ordinal() > o.getContext().getPriority().ordinal() ? 1
            			: this.getContext().getPriority().ordinal() < o.getContext().getPriority().ordinal() ? -1 : 0;
            }
        }
        
		@Override
		public IContext getContext() {
			if(object instanceof Contextful)
			{
				Contextful c = (Contextful) object;
				return c.getContext();
			}
			return null;
		}

		public boolean contextEquals(IContext context) 
		{
			if(context.equals(this.getContext()))
			{
				return true;
			}			
			return false;
		}		
    }
}
