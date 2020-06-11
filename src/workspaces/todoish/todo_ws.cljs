(ns todoish.todo-ws
  (:require [nubank.workspaces.core :as ws]
            [nubank.workspaces.card-types.fulcro3 :as ct.fulcro]
            [todoish.models.todo :as todo]
            [todoish.ui.components.new-todo-field :as new-todo-field]
            [todoish.ui.root :as r]))

(ws/defcard todo-card
  (ct.fulcro/fulcro-card
    {::ct.fulcro/root todo/Todo
     ::ct.fulcro/initial-state "Add more cards"}))

(ws/defcard new-todo-card
  (ct.fulcro/fulcro-card
    {::ct.fulcro/root new-todo-field/NewTodoField}))

(ws/defcard root-card
  (ct.fulcro/fulcro-card
    {::ct.fulcro/root r/Root
     ::ct.fulcro/wrap-root? false}))