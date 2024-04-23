package cn.swust.jiur.custom;

import androidx.annotation.NonNull;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.MarkwonPlugin;
import io.noties.markwon.html.HtmlPlugin;
import io.noties.markwon.html.HtmlTag;
import io.noties.markwon.html.MarkwonHtmlParser;

public class CustomHeaderPlugin extends AbstractMarkwonPlugin {
    @NonNull
    public static CustomHeaderPlugin create() {
        return new CustomHeaderPlugin();
    }

    public void configure(@NonNull Markwon.Builder builder) {
        builder.usePlugin(CustomHeaderPlugin.create());
    }

    private static class CustomHeaderTagPlugin {
        @NonNull
        static CustomHeaderTagPlugin create() {
            return new CustomHeaderTagPlugin();
        }

        private CustomHeaderTagPlugin() {
            //super("h1", "h2");
        }

//        @Override
//        public void configureHandler(@NonNull final HtmlTag tag) {
//            tag.setHandler(new SimpleTagHandler() {
//                @Override
//                public void handle(@NonNull Markwon markwon, @NonNull RenderProps renderProps, @NonNull HtmlTag tag) {
//                    // Do nothing to remove the default behavior
//                }
//            });
//        }
    }
}
