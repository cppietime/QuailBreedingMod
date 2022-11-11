import os

color_names = ["white", "black", "red", "yellow", "green", "blue", "brown", "gray", "pink", "lime", "light_blue", "orange", "purple", "cyan", "light_gray", "magenta"]
colors = ["WhiteSmoke", "gray10", "crimson", "gold", "MediumSpringGreen", "DodgerBlue", "Brown", "gray50", "pink", "LimeGreen", "LightBlue1", "Orange", "indigo", "turquoise", "gray75", "magenta"]

for i in range(16):
    #os.system("magick convert sand.png -fill %s -tint 100 %s_concrete_powder.png" % (colors[i], color_names[i]))
    #os.system("magick convert terracotta.png -fill %s -colorize 80 %s_concrete.png" % (colors[i], color_names[i]))
    #os.system("magick convert glass.png -fill %s -colorize 60 %s_glass.png" % (colors[i], color_names[i]))
    #os.system("magick convert terracotta.png -fill %s -tint 100 %s_terracotta.png" % (colors[i], color_names[i]))
    os.system("magick convert string.png -fill %s -colorize 50 %s_wool.png" % (colors[i], color_names[i]))