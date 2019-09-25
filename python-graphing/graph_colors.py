import csv
import matplotlib.pyplot as plt
import math
from os import path, listdir

fig = plt.figure()
ax1 = plt.subplot2grid((1, 1), (0, 0))

file_paths = [name for name in listdir('.') if path.isfile(name)]
file_paths.pop(file_paths.index("graph_colors.py"))

#TODO figure out how to loop through the 3000 files we generated with Java
outpath = f"C:\\Users\\legit\\Documents\\Research Data\\Proof of Concept\\Diffusion\\Filter Paper\\Red Video"

nameLoc = 17

#TODO Name this a method that is iterable so we can iterate through all files!
def get_data(file_path):
    red_values = []
    green_values = []
    blue_values = []
    x_locs = []
    
    with open(file_path, newline = "") as csvfile:
        reader = csv.reader(csvfile)
        #Skip headers
        next(reader, None)
        for row in reader:
            red_values.append(int(row[0]))
            green_values.append(int(row[1]))
            blue_values.append(int(row[2]))
            x_locs.append(int(row[3]))

    return [red_values, green_values, blue_values, x_locs]


def get_ticks(x_locs, num_of_ticks):
    pixel_range = x_locs[-1] - x_locs[0]
    ticks = []
    total = x_locs[0]
    ticks.append(total)
    for i in range(0, num_of_ticks):
        total += pixel_range / num_of_ticks
        ticks.append(total)
    
    return ticks
#TODO MAKE THIS ITERABLE
#file_paths is an array of file paths in strings
def save_graph_rgb(file_paths, outpath):
    name = file_paths[0][0:nameLoc]    
    
    red_v = get_data(file_paths[0])[0]
    green_v = get_data(file_paths[0])[1]
    blue_v = get_data(file_paths[0])[2]
    x_locs = get_data(file_paths[0])[3]
    
    lines = ax1.plot(x_locs, red_v, "r", x_locs, green_v, "g", x_locs, blue_v, "b")
    plt.setp(lines[0], color = "r", label = "Red")
    plt.setp(lines[1], color = "g", label = "Green")
    plt.setp(lines[2], color = "b", label = "Blue")
    ax1.set_yticks([0, 50, 100, 150, 200, 255])
    ax1.set_xticks(get_ticks(x_locs, 10))
    for label in ax1.xaxis.get_ticklabels():
        label.set_rotation(45)

    plt.subplots_adjust(left = 0.11,
                        right = 0.9,
                        bottom = 0.150,
                        top = 0.92,
                        hspace = 0.2,
                        wspace = 0.2)

    plt.xlabel("Pixel")
    plt.ylabel("RGB Value")
    plt.title(name)
    plt.grid()
    plt.legend()
    plt.draw()

    def save(name):
        fig.savefig(path.join(outpath, f"{name}.png"))
        print(f"Saved {name}")

        for i in range(1, len(file_paths)):

            name = file_paths[i][0:nameLoc]

            red_v = get_data(file_paths[i])[0]
            green_v = get_data(file_paths[i])[1]
            blue_v = get_data(file_paths[i])[2]
            x_locs = get_data(file_paths[i])[3]
            
            #TODO Set data can't take a list!!!
            lines[0].set_data(x_locs, red_v)
            lines[1].set_data(x_locs, green_v)
            lines[2].set_data(x_locs, blue_v)
            plt.title(name)
            plt.draw()
            fig.savefig(path.join(outpath, f"{name}.png"))
            print(f"Saved {name}")

    def show(i):

        name = file_paths[i][0:nameLoc] 

        red_v = get_data(file_paths[i])[0]
        green_v = get_data(file_paths[i])[1]
        blue_v = get_data(file_paths[i])[2]
        x_locs = get_data(file_paths[i])[3]
        
        #TODO Set data can't take a list!!!
        lines[0].set_data(x_locs, red_v)
        lines[1].set_data(x_locs, green_v)
        lines[2].set_data(x_locs, blue_v)
        plt.draw()
        
        print(f"Showing {name}")
        plt.show()

    #show(5)
    save(name)



save_graph_rgb(file_paths, outpath)

