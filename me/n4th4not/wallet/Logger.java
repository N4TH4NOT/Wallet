package me.n4th4not.wallet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public final class Logger {

    private final Main a;
    private FileOutputStream b = null;
    private boolean c = false;

    public Logger(Main var0) {
        this.a = var0;
        setEnable(var0.a.useLogger());
    }

    public void setEnable(boolean var0) {
        if (this.c == var0) return;
        if (this.b == null && var0) {
            boolean var1 = !Utilities.Dir.b.exists();
            Utilities.Dir.a(Utilities.Dir.b,true);
            try {
                this.b = new FileOutputStream(Utilities.Dir.b);
                if (var1) this.b.write(("########## " + Utilities.Text.b.format(new Date()) + " ##########\n").getBytes());
            }
            catch (FileNotFoundException ex) {this.b = null;}
            catch (IOException ex) {ex.printStackTrace();}
        }
        else if (this.b != null && !var0) {
            try {
                this.b.write(("########## " + Utilities.Text.b.format(new Date()) + " ##########\n").getBytes());
                this.b.flush();
                this.b.close();
            }
            catch (IOException ex) {ex.printStackTrace();}
        }
        this.c = this.b != null && var0;
    }

    public void clear() {
        if (this.c) {
            setEnable(false);
            Utilities.Dir.a(Utilities.Dir.c,false);
            Utilities.Dir.b.renameTo(new File(Utilities.Dir.c,Utilities.Text.a.format(new Date()) + ".log"));
            if (this.a.isEnabled()) setEnable(true);
        }
    }

    public void write(String var0) {
        if (!this.c) return;
        try {this.b.write(('[' + Utilities.Text.c.format(new Date()) + "] " + var0 + "\n").getBytes());}
        catch (IOException e) {this.a.getLogger().warning("[LOG] " + var0);}
    }
}
