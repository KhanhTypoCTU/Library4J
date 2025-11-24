package ctu.cict.khanhtypo.forms.component;

import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.function.Function;

public interface IBsonRepresentableComponent {
    Object getAsBsonValue();

    Component getComponent();

    static IBsonRepresentableComponent wrap(JTextComponent component) {
        return new IBsonRepresentableComponent() {
            @Override
            public Object getAsBsonValue() {
                return component.getText();
            }

            @Override
            public Component getComponent() {
                return component;
            }
        };
    }

    static <T extends Component> IBsonRepresentableComponent wrap(T component, Function<T, Object> bsonValueFunction) {
        return new IBsonRepresentableComponent() {
            @Override
            public Object getAsBsonValue() {
                return bsonValueFunction.apply(component);
            }

            @Override
            public T getComponent() {
                return component;
            }
        };
    }
}
