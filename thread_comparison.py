import matplotlib.pyplot as plt
import pandas

'''
run_data = pandas.read_csv("thread_data10k6k.data", sep=';', index_col=0)
run_data["pool size"].plot()
plt.show()
plt.close()
'''


cached_run_data = pandas.read_csv("cached_run_data_1000-50.data", sep=';', index_col=0)
cached_thread_data = pandas.read_csv("cached_thread_data_1000-50.data", sep=';', index_col=0).groupby('Concurrent tasks')
fixed_run_data = pandas.read_csv("fixed_run_data_1000-50.data", sep=';', index_col=0)
fixed_thread_data = pandas.read_csv("fixed_thread_data_1000-50.data", sep=';', index_col=0).groupby('Concurrent tasks')
cached_thread_generated_10k6k = pandas.read_csv("thread_data10k6k.data", sep=';', index_col=0)
cached_thread_generated_10k6k_unlimited = pandas.read_csv("thread_data_unlimited_10k6k.data", sep=';')
cached_thread_generated_unlimited = pandas.read_csv("thread_data_unlimited.data", sep=';')


ax=cached_run_data.plot()
plot = fixed_run_data.plot(ax=ax)
plt.gcf().suptitle("Total runtime")
plot.set_ylabel("nanoseconds")
plot.legend(["Cached thread pool", "Fixed thread pool"])
plt.show()
plt.close()

ax = cached_thread_data['average runtime'].mean().plot()
plot = fixed_thread_data['average runtime'].mean().plot(ax=ax)
plt.gcf().suptitle("Average runtime of threads")
plot.set_ylabel("nanoseconds")
plot.legend(["Cached thread pool", "Fixed thread pool"])
plt.show()
plt.close()

ax = cached_thread_generated_10k6k["pool size"].plot()
plot = cached_thread_data["pool size"].mean().plot(ax=ax)
plt.gcf().suptitle("Threads generated by CachedThreadPool with limiter")
plot.set_ylabel("Threads")
plot.legend(["10k tasks with 6k deadline", "1k tasks with 600 deadline"])
plt.show()
plt.close()

ax = cached_thread_generated_10k6k_unlimited["pool size"].plot()
plot = cached_thread_generated_unlimited["pool size"].plot(ax=ax)
plt.gcf().suptitle("Threads generated by CachedThreadPool without limiter")
plot.set_ylabel("Threads")
plot.legend(["10k tasks with 6k deadline", "1k tasks with 600 deadline"])
plt.show()
plt.close()


