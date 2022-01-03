import matplotlib.pyplot as plt
import pandas as pd
import sys
import glob
import numpy as np
import ntpath
import os
from matplotlib import cm
from mpl_toolkits.mplot3d import Axes3D
import subprocess
import shutil

def lineplot(plotname, data):
    fig = plt.figure()
    ax = fig.add_subplot(111)
    ax.plot(data.iloc[:, 0], data.iloc[:, 1])
    ax.set_xlabel(data.columns[0])
    ax.set_ylabel(data.columns[1])
    ax.set_title(plotname.replace('_', ' '))
    fig.savefig(f'plots/{plotname}')
    plt.show()
    return

def surfaceplot(plotname, data, generate_video=True):
    fig = plt.figure()
    ax = fig.add_subplot(111, projection='3d')

    X, Y, Z = data.iloc[:, 0], data.iloc[:, 1], data.iloc[:, 2]
    shape = len(set(Y)), len(set(X))
    sh = lambda x: np.reshape(np.array(x), shape)
    X, Y, Z = sh(X), sh(Y), sh(Z)
    ax.plot_surface(X, Y, Z,
                    cmap='RdYlGn', linewidth=0, antialiased=True)
    # ax.plot(data.iloc[:, 1], data.iloc[:, 2])

    ax.set_xlabel(data.columns[0])
    ax.set_ylabel(data.columns[1])
    ax.set_zlabel('Win rate')
    ax.set_title(plotname.replace('_', ' '))

    ax.view_init(30, 135)
    fig.savefig(f'plots/{plotname}')
    plt.show()

    if generate_video:
        frames_dir = 'plot_video/temp'
        if os.path.isdir(frames_dir): raise Exception("temp dir already exists")
        os.mkdir(frames_dir)

        for angle in range(0, 360):
            ax.view_init(30, angle)
            plt.draw()
            plt.pause(.001)
            fig.savefig(f'{frames_dir}/{angle}')
            print(f'\rgerate image {angle}')
        print('\ngenerate video')
        subprocess.call("plot_video/generate_videos.sh")
        os.rename('plot_video/temp.mp4', f'{plotname}.mp4')
        shutil.rmtree('plot_video/temp', ignore_errors=True)
    return

def barplot(plotname, data):
    labels = data.iloc[:, 0]
    men_means = data.iloc[:, 1]
    women_means = data.iloc[:, 2]

    x = np.arange(len(labels))  # the label locations
    width = 0.35  # the width of the bars

    fig, ax = plt.subplots()
    rects1 = ax.bar(x - width / 2, men_means, width, label='avg. number of turns per game')
    rects2 = ax.bar(x + width / 2, women_means, width, label='avg. game realtime duration (ms)')

    ax.set_title(plotname)
    ax.set_xticks(x)
    ax.set_xticklabels(labels)
    ax.set_ylim(0,1000)
    ax.legend()

    def autolabel(rects):
        """Attach a text label above each bar in *rects*, displaying its height."""
        for rect in rects:
            height = rect.get_height()
            ax.annotate('{}'.format(height),
                        xy=(rect.get_x() + rect.get_width() / 2, min(height,900)),
                        xytext=(0, 3),  # 3 points vertical offset
                        textcoords="offset points",
                        ha='center', va='bottom')
    ax.annotate('depth: 6,\niterations: 100', xy=(2.1,700))
    ax.arrow(x=2.4, y=680, dx=0.4, dy=-300, head_width=0.1, head_length=30)
    autolabel(rects1)
    autolabel(rects2)

    fig.tight_layout()
    plt.savefig(f'plots/{plotname}')
    plt.show()

def generate_plot_based_on_data_dim(filename, data):
    if data.shape[1] == 2:
        lineplot(filename, data)

    if data.shape[1] == 3:
        surfaceplot(filename, data)

    if data.shape[1] == 4:
        raise Exception('heat')

    raise Exception(f'got csv of unexpected dimention {data.shape}')

def path_to_name(p):
    return os.path.splitext(ntpath.basename(p))[0]

def autoprocess_plot_data():
    args = sys.argv
    csvs = {}


    if len(args) == 1:
        for f in glob.glob('plot_data/*.csv'):
            csvs[path_to_name(f)] = pd.read_csv(f)
    elif len(args) == 2:
        csvs[path_to_name(args[1])] = pd.read_csv(args[1])
    else:
        print('Wrong arguments. Expected none or a file with plot data.')
        sys.exit(1)

    for k, v in csvs.items():
        generate_plot_based_on_data_dim(k, v)

def generate_test_data():
    x = np.arange(0, 10, 0.1)
    y = np.sin(x)

    data = pd.DataFrame(data=np.vstack([x,y]).T,
                 columns=['x val', 'y val'])
    data.to_csv('plot_data/test_plot_1.csv', index=False)

    x = np.arange(0, 10, 1)
    y = np.arange(0, 10, 1)
    x_v = []
    y_v = []
    z_v = []
    for x_ in x:
        for y_ in y:
            x_v.append(x_)
            y_v.append(y_)
            z_v.append(np.sin(x_/2) + np.cos(y_/3))
    x_v, y_v, z_v = np.array(x_v), np.array(y_v), np.array(z_v)
    data = pd.DataFrame(data=np.vstack([x_v, y_v, z_v]).T,
                        columns=['x val', 'y val', 'z val'])
    data.to_csv('plot_data/test_plot_2.csv', index=False)

def process_files(commands):
    for file, params in commands.items():
        plot_type, plot_name = params
        print(f'trying to create {plot_type} plot for {file}.')
        data = pd.read_csv(file)
        plot_type_map = {'bar': barplot,
                         'surface': surfaceplot,
                         'line': lineplot}
        plot_f = plot_type_map.get(plot_type)
        plot_f(plot_name, data)
        print('plot creation completed')

if __name__ == '__main__':
    #generate_test_data()
    #autoprocess_plot_data()
    plot_comands = \
        {'../../data/all_vs_random.csv':('bar', 'Everyone vs Random'),
         '../../data/montecarlo_greedy_data.csv':('surface', '(parameterized) Montecarlo vs Greedy')}
    process_files(plot_comands)

