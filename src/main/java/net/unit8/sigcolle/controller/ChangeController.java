package net.unit8.sigcolle.controller;

import javax.inject.Inject;
import javax.transaction.Transactional;

import enkan.collection.Multimap;
import enkan.component.doma2.DomaProvider;
import enkan.data.Flash;
import enkan.data.HttpResponse;
import enkan.data.Session;
import kotowari.component.TemplateEngine;
import net.unit8.sigcolle.auth.LoginUserPrincipal;
import net.unit8.sigcolle.dao.UserDao;
import net.unit8.sigcolle.form.RegisterForm;
import net.unit8.sigcolle.model.User;

import static enkan.util.HttpResponseUtils.RedirectStatusCode.SEE_OTHER;
import static enkan.util.HttpResponseUtils.redirect;

/**
 * @author takahashi
 */
public class ChangeController {
    @Inject
    private TemplateEngine templateEngine;

    @Inject
    private DomaProvider domaProvider;

    private static final String EMAIL_ALREADY_EXISTS = "このメールアドレスは既に登録されています。";

    /**
     * ユーザー登録情報変更画面表示.
     *
     * @return HttpResponse
     */
    public HttpResponse index2(Session session) {
        UserDao userdao = domaProvider.getDao(UserDao.class);
        LoginUserPrincipal principal = (LoginUserPrincipal) session.get("principal");
        User user = userdao.selectByUserId(principal.getUserId());
        RegisterForm form = new RegisterForm();
        form.setFirstName(user.getFirstName());
        form.setLastName(user.getLastName());
        form.setEmail(user.getEmail());
        form.setPass(user.getPass());
        return templateEngine.render("user/registerChange", "user", form);
    }





    /**
     * ユーザー登録処理.
     *
     * @param form 画面入力されたユーザー情報
     * @return HttpResponse
     */
    @Transactional
    public HttpResponse listUserInfo(RegisterForm form) {

        if (form.hasErrors()) {
            return templateEngine.render("user/registerChange", "user", form);
        }

        UserDao userDao = domaProvider.getDao(UserDao.class);

        // メールアドレス重複チェック
        if (userDao.countByEmail(form.getEmail()) != 0) {
            form.setErrors(Multimap.of("email", EMAIL_ALREADY_EXISTS));
            return templateEngine.render("user/register",
                    "user", form
            );
        }

        User user = new User();
        user.setLastName(form.getLastName());
        user.setFirstName(form.getFirstName());
        user.setEmail(form.getEmail());
        user.setPass(form.getPass());

        userDao.update(user);

        Session session = new Session();

        session.put(
                "principal",
                new LoginUserPrincipal(user.getUserId(), user.getLastName() + " " + user.getFirstName())
        );



        HttpResponse response = redirect("/", SEE_OTHER);
        response.setFlash(new Flash<>("変更しました！"));

        return response;
    }
}
